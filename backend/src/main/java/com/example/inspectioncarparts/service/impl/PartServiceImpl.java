package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.mapper.PartMapper;
import com.example.inspectioncarparts.mapper.PartTypeMapper;
import com.example.inspectioncarparts.mapper.VehicleMapper;
import com.example.inspectioncarparts.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PartServiceImpl implements PartService {
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
        
        partMapper.insert(part);
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
    public void deletePart(Long id) {
        // 检查是否有子配件
        if (partMapper.selectCount(new QueryWrapper<Part>().eq("parent_id", id)) > 0) {
            throw new RuntimeException("存在子配件，无法删除");
        }
        
        partMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Part getPartById(Long id) {
        return partMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Part> listParts(int pageNum, int pageSize) {
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Part> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        
        // 执行分页查询
        page = partMapper.selectPage(page, null);

        return Page.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Part> getPartTree(Long rootId) {
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
    public Part associateWithVehicle(Long partId, Long vehicleId) {
        Part part = partMapper.selectById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }
        
        if (vehicleId != null && vehicleMapper.selectById(vehicleId) == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        part.setVehicleId(vehicleId);
        partMapper.updateById(part);
        return part;
    }

    @Override
    @Transactional
    public Part disassociateFromVehicle(Long partId) {
        return associateWithVehicle(partId, null);
    }

    @Override
    @Transactional(readOnly = true)
    public String generateQrCode(Long partId) {
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
    public List<Part> batchAssociateWithVehicle(Long vehicleId, List<Long> partIds) {
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
        for (Long partId : partIds) {
            associateWithVehicle(partId, vehicleId);
        }
//        // 批量更新配件的vehicleId
        List<Part> updatedParts = new ArrayList<>();
//        for (Part part : parts) {
//            part.setVehicleId(vehicleId);
//            partMapper.updateById(part);
//            updatedParts.add(part);
//        }
        
        return updatedParts;
    }

    @Override
    @Transactional
    public void disassociateAllFromVehicle(Long vehicleId) {
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
    public List<Part> listByVehicleAndType(Long vehicleId, Long partTypeId) {
        QueryWrapper<Part> queryWrapper = new QueryWrapper<>();
        
        if (vehicleId != null) {
            queryWrapper.eq("vehicle_id", vehicleId);
        }
        
        if (partTypeId != null) {
            queryWrapper.eq("type_id", partTypeId);
        }
        
        return partMapper.selectList(queryWrapper);
    }
}