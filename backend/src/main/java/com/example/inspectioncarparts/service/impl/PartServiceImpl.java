package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.dto.ReplacePartRequest;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.domain.entity.Vehicle;
import com.example.inspectioncarparts.domain.enums.PartOperationType;
import com.example.inspectioncarparts.domain.enums.PartStatus;
import com.example.inspectioncarparts.mapper.PartMapper;
import com.example.inspectioncarparts.mapper.PartTypeMapper;
import com.example.inspectioncarparts.mapper.VehicleMapper;
import com.example.inspectioncarparts.service.PartOperationHistoryService;
import com.example.inspectioncarparts.service.PartService;
import com.example.inspectioncarparts.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartServiceImpl implements PartService {

    @Autowired
    private PartOperationHistoryService partOperationHistoryService;
    
    @Autowired
    private PartMapper partMapper;
    
    @Autowired
    private PartTypeMapper partTypeMapper;
    
    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public Part createPart(Part part) {
        // 检查配件编号是否已存在
        if (partMapper.selectCount(new QueryWrapper<Part>()
                .eq("part_code", part.getPartCode())) > 0) {
            throw new RuntimeException("配件编号已存在");
        }
        
        // 检查配件类型是否存在
        if (part.getTypeId() != null && partTypeMapper.selectById(part.getTypeId()) == null) {
            throw new RuntimeException("配件类型不存在");
        }
        
        // 检查父配件是否存在
        if (part.getParentId() != null && partMapper.selectById(part.getParentId()) == null) {
            throw new RuntimeException("父配件不存在");
        }
        
        // 检查车辆是否存在
        if (part.getVehicleId() != null && vehicleMapper.selectById(part.getVehicleId()) == null) {
            throw new RuntimeException("车辆不存在");
        }

        Vehicle vehicle = vehicleMapper.selectById(part.getVehicleId());
        partMapper.insert(part);

        // 记录操作历史
        String creator = SecurityUtils.getRequiredLoginUser() != null ?  SecurityUtils.getRequiredLoginUser().getRealName() : null;
        partOperationHistoryService.recordOperation(part.getId(), PartOperationType.RECEIVE, creator, "设备领用", null);

        // 如果配件有vehicleId, 则写入操作历史配件以绑定车辆XXX
        if (part.getVehicleId() != null) {
            partOperationHistoryService.recordOperation(part.getId(),
                    PartOperationType.ASSOCIATE_VEHICLE,
                    creator,
                    String.format("绑定车辆[%s]",vehicle.getVehicleName()),
                    part.getVehicleId());
        }
        return part;
    }

    @Override
    @Transactional
    public Part updatePart(Part part) {
        Part existing = partMapper.selectById(part.getId());
        if (existing == null) {
            throw new RuntimeException("配件不存在");
        }
        
        // 检查配件编号是否已存在（排除自己）
        if (!existing.getPartCode().equals(part.getPartCode()) && 
            partMapper.selectCount(new QueryWrapper<Part>()
                .eq("part_code", part.getPartCode())) > 0) {
            throw new RuntimeException("配件编号已存在");
        }
        
        // 检查配件类型是否存在
        if (part.getTypeId() != null && partTypeMapper.selectById(part.getTypeId()) == null) {
            throw new RuntimeException("配件类型不存在");
        }
        
        // 检查父配件是否存在
        if (part.getParentId() != null && partMapper.selectById(part.getParentId()) == null) {
            throw new RuntimeException("父配件不存在");
        }
        
        // 检查车辆是否存在
        if (part.getVehicleId() != null && vehicleMapper.selectById(part.getVehicleId()) == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        partMapper.updateById(part);
        return part;
    }

    @Override
    @Transactional
    public void deletePart(Integer id) {
        // 检查是否有子配件
        if (partMapper.selectCount(new QueryWrapper<Part>().eq("parent_id", id)) > 0) {
            throw new RuntimeException("存在子配件，无法删除");
        }
        
        partMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Part getPartById(Integer id) {
        return partMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Part> listParts(int pageNum, int pageSize, Integer bindStatus) {
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Part> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        
        // 执行分页查询

        QueryWrapper<Part> queryWrapper = new QueryWrapper<>();
        if (bindStatus != null && bindStatus == 0) {
            queryWrapper.isNull("vehicle_id");
        }
        page = partMapper.selectPage(page, queryWrapper);

        return Page.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Part> getPartTree(Integer rootId) {
        List<Part> result = new ArrayList<>();
        if (rootId == null) {
            // 查询所有根节点
            List<Part> roots = partMapper.selectList(new QueryWrapper<Part>().isNull("parent_id"));
            for (Part root : roots) {
                buildTree(root, result);
            }
        } else {
            // 查询指定根节点
            Part root = partMapper.selectById(rootId);
            if (root != null) {
                buildTree(root, result);
            }
        }
        return result;
    }

    private void buildTree(Part parent, List<Part> result) {
        result.add(parent);
        List<Part> children = partMapper.selectList(new QueryWrapper<Part>().eq("parent_id", parent.getId()));
        for (Part child : children) {
            buildTree(child, result);
        }
    }

    @Override
    @Transactional
    public Part associateWithVehicle(Integer partId, Integer vehicleId, Integer parentId) {
        Part part = partMapper.selectById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }
        
        if (vehicleId != null && vehicleMapper.selectById(vehicleId) == null) {
            throw new RuntimeException("车辆不存在");
        }

        partMapper.update(null, new UpdateWrapper<Part>()
                .eq("id", partId)
                .set("vehicle_id", vehicleId)
                .set("parent_id", parentId)
        );

        // 如果车辆ID不为空，则写入操作历史，配件和车辆绑定
        if (vehicleId != null) {
            Vehicle vehicle = vehicleMapper.selectById(vehicleId);
            partOperationHistoryService.recordOperation(partId,
                    PartOperationType.ASSOCIATE_VEHICLE,
                    SecurityUtils.getRealName(),
                    String.format("绑定车辆[%s]",vehicle.getVehicleName()),
                    vehicleId);
        } else {
            Vehicle vehicle = vehicleMapper.selectById(part.getVehicleId());
            partOperationHistoryService.recordOperation(partId,
                    PartOperationType.DISASSOCIATE_VEHICLE,
                    SecurityUtils.getRealName(),
                    String.format("解绑车辆[%s]",vehicle.getVehicleName()),
                    part.getVehicleId());
        }

        part.setVehicleId(vehicleId);
        return part;
    }

    @Override
    @Transactional
    public Part disassociateFromVehicle(Integer partId) {
        return associateWithVehicle(partId, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateQrCode(Integer partId) {
        Part part = partMapper.selectById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }
        return "part://" + part.getPartCode();
    }

    @Override
    @Transactional(readOnly = true)
    public Part parseQrCode(String qrCode) {
        if (!qrCode.startsWith("part://")) {
            throw new RuntimeException("无效的二维码格式");
        }
        
        String partCode = qrCode.substring(7);
        return partMapper.selectOne(new QueryWrapper<Part>().eq("part_code", partCode));
    }

    @Override
    @Transactional
    public List<Part> batchAssociateWithVehicle(Integer vehicleId, List<Integer> partIds) {
        // 检查车辆是否存在
        if (vehicleId != null && vehicleMapper.selectById(vehicleId) == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        // 检查所有配件是否存在
        List<Part> parts = partMapper.selectBatchIds(partIds);
        if (parts.size() != partIds.size()) {
            throw new RuntimeException("部分配件不存在");
        }
        // 存在车辆已关联的配件，删除部分配件后重新关联
        List<Part> existingParts = partMapper.selectList(
            new QueryWrapper<Part>().eq("vehicle_id", vehicleId));
        for (Part part : existingParts) {
            if (!partIds.contains(part.getId())) {
                UpdateWrapper<Part> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq("id", part.getId())
                        .set("vehicle_id", null);
                partMapper.update(null, updateWrapper);
            }
        }

        // 批量关联
        for (Integer partId : partIds) {
            associateWithVehicle(partId, vehicleId, null);
        }
        
        return parts;
    }

    @Override
    @Transactional
    public void disassociateAllFromVehicle(Integer vehicleId) {
        // 检查车辆是否存在
        if (vehicleMapper.selectById(vehicleId) == null) {
            throw new RuntimeException("车辆不存在");
        }

        // 使用UpdateWrapper直接更新所有关联配件的vehicle_id为null
        UpdateWrapper<Part> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("vehicle_id", vehicleId)
                   .set("vehicle_id", null);

        // 执行批量更新
        partMapper.update(null, updateWrapper);
    }

    @Override
    public Part getPartByCode(String partCode) {
        return partMapper.selectOne(new QueryWrapper<Part>().eq("part_code", partCode));
    }

    @Override
    public List<Part> listByVehicleAndType(Integer vehicleId, Integer partTypeId) {
        QueryWrapper<Part> queryWrapper = new QueryWrapper<>();

        if (vehicleId != null) {
            queryWrapper.eq("vehicle_id", vehicleId);
        }

        if (partTypeId != null) {
            queryWrapper.eq("type_id", partTypeId);
        }

        return partMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public Part replacePart(Integer oldPartId, Part newPart) {
        // 1. 检查原配件是否存在
        Part oldPart = partMapper.selectById(oldPartId);
        if (oldPart == null) {
            throw new RuntimeException("原配件不存在");
        }

        // 2. 解绑原配件与车辆的关联
        disassociateFromVehicle(oldPartId);

        // 3. 保存新配件
        // 如果新配件没有指定车辆ID，使用原配件的车辆ID
        if (newPart.getVehicleId() == null && oldPart.getVehicleId() != null) {
            newPart.setVehicleId(oldPart.getVehicleId());
        }

        // 创建新配件
        Part created = createPart(newPart);

        return created;
    }

    @Override
    @Transactional
    public Part receivePart(Integer partId, String operator) {
        // 获取配件信息
        Part part = partMapper.selectById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }

        // 检查配件当前状态
        if ("2".equals(part.getStatus())) {
            throw new RuntimeException("配件已经处于领用状态");
        }

//        // 更新配件状态为"已领用"
//        part.setStatus("2"); // 2表示已领用状态
//        partMapper.updateById(part);

        // 记录操作历史
        partOperationHistoryService.recordOperation(
            partId,
            PartOperationType.RECEIVE,
            operator,
            "配件领用",
            null
        );

        return part;
    }

    @Override
    @Transactional
    public Part repairPart(Integer partId, String operator, String remark) {
        // 获取配件信息
        Part part = partMapper.selectById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }

        // 检查配件当前状态
//        if ("3".equals(part.getStatus())) {
//            throw new RuntimeException("配件已经处于维修状态");
//        }

        // 更新配件状态为"维修中"
//        part.setStatus("3"); // 3表示维修中状态
//        partMapper.updateById(part);

        // 记录操作历史
        partOperationHistoryService.recordOperation(
            partId,
            PartOperationType.REPAIR,
            operator,
            remark,
            null
        );

        return part;
    }

    @Override
    @Transactional
    public Part scrapPart(Integer partId, String remark) {
        // 1. 获取配件信息
        Part part = getPartById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }

        // 2. 如果配件绑定了车辆，先解绑
        if (part.getVehicleId() != null) {
            disassociateFromVehicle(partId);
        }

        // 3. 更新配件状态为报废
        UpdateWrapper<Part> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", partId);
        updateWrapper.set("status", PartStatus.SCRAPPED.getCode());
        updateWrapper.set("remark", remark);
        partMapper.update(null, updateWrapper);

        // 记录操作历史
        partOperationHistoryService.recordOperation(
            partId,
            PartOperationType.SCRAP,
            SecurityUtils.getRealName(),
            remark,
            part.getVehicleId()
        );

        return part;
    }

    @Override
    @Transactional
    public Part replacePart(ReplacePartRequest request) {
        boolean replaceExist = request.getNewPartId() != null;
        Part newPart = request.getNewPart();
        // 查询老配件
        Part oldPart = partMapper.selectById(request.getOldPartId());
        if (oldPart == null) {
            throw new RuntimeException("原配件不存在");
        }
        Integer oldVehicleId = oldPart.getVehicleId();
        Integer oldParentId = oldPart.getParentId();
        // 1. 解绑原配件
        disassociateFromVehicle(request.getOldPartId());

        if (replaceExist) {
            // 更换已有配件 2.1 检查新配件是否存在
            newPart = partMapper.selectById(request.getNewPartId());
            if (newPart == null) {
                throw new RuntimeException("新配件不存在");
            }

        }
        // 2. 保存新配件
        newPart.setVehicleId(oldVehicleId);
        newPart.setParentId(oldParentId);
        Part created = replaceExist ? updatePart(newPart) : createPart(newPart);

        return created;
    }
}
