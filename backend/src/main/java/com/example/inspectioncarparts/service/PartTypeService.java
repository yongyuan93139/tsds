package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.PartType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PartTypeService {
    /**
     * 创建配件类型
     * @param partType 配件类型信息
     * @return 创建后的配件类型
     */
    PartType createPartType(PartType partType);

    /**
     * 更新配件类型
     * @param partType 配件类型信息
     * @return 更新后的配件类型
     */
    PartType updatePartType(PartType partType);

    /**
     * 删除配件类型
     * @param id 配件类型ID
     */
    void deletePartType(Long id);

    /**
     * 获取配件类型详情
     * @param id 配件类型ID
     * @return 配件类型信息
     */
    PartType getPartTypeById(Long id);

    /**
     * 分页查询配件类型
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 配件类型分页对象
     */
    Page<PartType> listPartTypes(int pageNum, int pageSize);

    /**
     * 批量导入配件类型
     * @param file Excel文件
     * @return 导入结果
     */
    boolean importPartTypes(MultipartFile file);

    /**
     * 检查类型编号是否已存在
     * @param typeCode 类型编号
     * @return 是否存在
     */
    boolean checkTypeCodeExists(String typeCode);

    /**
     * 检查类型名称是否已存在
     * @param typeName 类型名称
     * @return 是否存在
     */
    boolean checkTypeNameExists(String typeName);
}