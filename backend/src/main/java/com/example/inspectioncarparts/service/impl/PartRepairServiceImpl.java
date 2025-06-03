package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.domain.entity.PartRepair;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import com.example.inspectioncarparts.domain.enums.PartStatus;
import com.example.inspectioncarparts.mapper.PartRepairMapper;
import com.example.inspectioncarparts.service.PartRepairService;
import com.example.inspectioncarparts.service.PartService;
import com.example.inspectioncarparts.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 配件维修服务实现类
 */
@Service
public class PartRepairServiceImpl implements PartRepairService {

    @Autowired
    private PartRepairMapper partRepairMapper;

    @Autowired
    private PartService partService;

    /**
     * 生成维修工单号
     * 格式：WX + yyyyMMddHHmmss + 3位随机数
     */
    private String generateRepairOrderNo() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomNum = String.format("%03d", new Random().nextInt(1000));
        return "WX" + timestamp + randomNum;
    }

    @Override
    @Transactional
    public PartRepair createRepair(PartRepair partRepair) {
        // 验证配件是否存在
        Part part = partService.getPartById(partRepair.getPartId());
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }

        // 生成并设置维修工单号
        String repairOrderNo = generateRepairOrderNo();
        partRepair.setRepairOrderNo(repairOrderNo);

        // 设置初始状态和创建时间
        partRepair.setStatus(0); // 待处理状态
        partRepair.setCreateTime(LocalDateTime.now());
        partRepair.setCreator(SecurityUtils.getRealName());

        // 更新配件状态为维修中
        part.setStatus(PartStatus.IN_REPAIR.getCode());
        partService.updatePart(part);

        // 保存维修工单
        partRepairMapper.insert(partRepair);
        return partRepair;
    }

    @Override
    @Transactional
    public PartRepair updateRepair(PartRepair partRepair) {
        // 验证维修工单是否存在
        PartRepair existingRepair = getRepairById(partRepair.getId());
        if (existingRepair == null) {
            throw new RuntimeException("维修工单不存在");
        }

        // 更新维修工单
        partRepairMapper.updateById(partRepair);
        return getRepairById(partRepair.getId());
    }

    @Override
    public PartRepair getRepairById(Long id) {
        return partRepairMapper.selectById(id);
    }

    @Override
    public com.example.inspectioncarparts.common.Page<PartRepair> listRepairs(int pageNum, int pageSize) {
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PartRepair> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        page = partRepairMapper.selectPage(page, null);
        return Page.from(page);
    }

    @Override
    @Transactional
    public PartRepair processRepair(Long id, String processResult, String faultReason, String operator) {
        // 验证维修工单是否存在
        PartRepair repair = getRepairById(id);
        if (repair == null) {
            throw new RuntimeException("维修工单不存在");
        }

        // 更新维修工单状态和处理信息
        repair.setStatus(1); // 已处理状态
        repair.setProcessTime(LocalDateTime.now());
        repair.setProcessResult(processResult);
        repair.setFaultReason(faultReason);
        repair.setOperator(operator);

        // 更新维修工单
        partRepairMapper.updateById(repair);

        // 更新配件状态为可用
        Part part = partService.getPartById(repair.getPartId());
        if (part != null) {
            part.setStatus(PartStatus.ENABLED.getCode());
            partService.updatePart(part);
        }

        return repair;
    }

    @Override
    public com.example.inspectioncarparts.common.Page<PartRepair> listRepairsByPartId(Integer partId, int pageNum, int pageSize) {
        // 验证配件是否存在
        Part part = partService.getPartById(partId);
        if (part == null) {
            throw new RuntimeException("配件不存在");
        }
        // 查询该配件的维修记录
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PartRepair> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PartRepair> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PartRepair::getPartId, partId);
        queryWrapper.orderByDesc(PartRepair::getCreateTime);

        page = partRepairMapper.selectPage(page, queryWrapper);
        return Page.from(page);
    }
}
