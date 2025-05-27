package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.domain.entity.Part;
import com.example.inspectioncarparts.common.Page;
import java.util.List;

public interface PartService {
    /**
     * 创建配件信息
     * @param part 配件信息
     * @return 创建后的配件信息
     */
    Part createPart(Part part);

    /**
     * 更新配件信息
     * @param part 配件信息
     * @return 更新后的配件信息
     */
    Part updatePart(Part part);

    /**
     * 删除配件信息
     * @param id 配件ID
     */
    void deletePart(Integer id);

    /**
     * 获取配件详情
     * @param id 配件ID
     * @return 配件信息
     */
    Part getPartById(Integer id);

    /**
     * 分页查询配件信息
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 配件列表
     */
    Page<Part> listParts(int pageNum, int pageSize);

    /**
     * 获取配件树形结构
     * @param rootId 根节点ID
     * @return 配件树
     */
    List<Part> getPartTree(Integer rootId);

    /**
     * 关联配件到车辆
     * @param partId 配件ID
     * @param vehicleId 车辆ID
     * @return 更新后的配件信息
     */
    Part associateWithVehicle(Integer partId, Integer vehicleId);

    /**
     * 解除配件与车辆的关联
     * @param partId 配件ID
     * @return 更新后的配件信息
     */
    Part disassociateFromVehicle(Integer partId);

    /**
     * 生成配件二维码
     * @param partId 配件ID
     * @return 二维码内容
     */
    String generateQrCode(Integer partId);

    /**
     * 解析二维码获取配件信息
     * @param qrCode 二维码内容
     * @return 配件信息
     */
    Part parseQrCode(String qrCode);
    
    /**
     * 根据车辆ID和配件类型查询配件列表
     * @param vehicleId 车辆ID
     * @param partTypeId 配件类型ID
     * @return 配件列表
     */
    List<Part> listByVehicleAndType(Integer vehicleId, Integer partTypeId);

    /**
     * 批量关联配件到车辆
     * @param vehicleId 车辆ID
     * @param partIds 配件ID列表
     * @return 更新后的配件列表
     */
    List<Part> batchAssociateWithVehicle(Integer vehicleId, List<Integer> partIds);

    /**
     * 根据车辆ID解除所有配件关联
     * @param vehicleId 车辆ID
     */
    void disassociateAllFromVehicle(Integer vehicleId);

    Part getPartByCode(String partCode);
}