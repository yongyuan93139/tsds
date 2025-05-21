package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.domain.entity.PartType;
import com.example.inspectioncarparts.mapper.PartMapper;
import com.example.inspectioncarparts.mapper.PartTypeMapper;
import com.example.inspectioncarparts.service.PartTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class PartTypeServiceImpl implements PartTypeService {
    @Autowired
    private PartTypeMapper partTypeMapper;
    
    @Autowired
    private PartMapper partMapper;

    @Override
    @Transactional
    public PartType createPartType(PartType partType) {
        if (checkTypeCodeExists(partType.getTypeCode())) {
            throw new RuntimeException("配件类型编号已存在");
        }
        if (checkTypeNameExists(partType.getTypeName())) {
            throw new RuntimeException("配件类型名称已存在");
        }
        partTypeMapper.insert(partType);
        return partType;
    }

    @Override
    @Transactional
    public PartType updatePartType(PartType partType) {
        PartType existing = partTypeMapper.selectById(partType.getId());
        if (existing == null) {
            throw new RuntimeException("配件类型不存在");
        }
        if (!existing.getTypeCode().equals(partType.getTypeCode()) && 
            checkTypeCodeExists(partType.getTypeCode())) {
            throw new RuntimeException("配件类型编号已存在");
        }
        if (!existing.getTypeName().equals(partType.getTypeName()) && 
            checkTypeNameExists(partType.getTypeName())) {
            throw new RuntimeException("配件类型名称已存在");
        }
        partTypeMapper.updateById(partType);
        return partType;
    }

    @Override
    @Transactional
    public void deletePartType(Long id) {
        if (partMapper.selectCount(new QueryWrapper<Part>().eq("type_id", id)) > 0) {
            throw new RuntimeException("存在关联配件，无法删除");
        }
        partTypeMapper.deleteById(id);
    }

    @Override
    public PartType getPartTypeById(Long id) {
        return partTypeMapper.selectById(id);
    }

    @Override
    public Page<PartType> listPartTypes(int pageNum, int pageSize) {
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<PartType> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        
        // 执行分页查询
        partTypeMapper.selectPage(page, null);
        
//        // 检查count查询结果是否准确
//        if (page.getTotal() == 0 && !page.getRecords().isEmpty()) {
//            // 手动执行count查询
//            int total = partTypeMapper.selectCount(null);
//            page.setTotal(total);
//            // 重新计算总页数
//            page.setPages((total + pageSize - 1) / pageSize);
//        }
        
        return Page.from(page);
    }

    @Override
    public boolean importPartTypes(MultipartFile file) {
        // TODO: 实现Excel导入逻辑
        return false;
    }

    @Override
    public boolean checkTypeNameExists(String typeName) {
        return partTypeMapper.selectCount(new QueryWrapper<PartType>()
                .eq("type_name", typeName)) > 0;
    }

    @Override
    public boolean checkTypeCodeExists(String typeCode) {
        return partTypeMapper.selectCount(new QueryWrapper<PartType>()
                .eq("type_code", typeCode)) > 0;
    }
}