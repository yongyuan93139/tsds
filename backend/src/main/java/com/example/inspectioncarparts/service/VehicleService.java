package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.domain.entity.Vehicle;
import com.example.inspectioncarparts.common.Page;
import java.util.List;

public interface VehicleService {
    /**
     * 创建车辆信息
     * @param vehicle 车辆信息
     * @return 创建后的车辆信息
     */
    Vehicle createVehicle(Vehicle vehicle);

    /**
     * 更新车辆信息
     * @param vehicle 车辆信息
     * @return 更新后的车辆信息
     */
    Vehicle updateVehicle(Vehicle vehicle);

    /**
     * 删除车辆信息
     * @param id 车辆ID
     */
    void deleteVehicle(Long id);

    /**
     * 获取车辆详情
     * @param id 车辆ID
     * @return 车辆信息
     */
    Vehicle getVehicleById(Long id);

    /**
     * 分页查询车辆信息
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 车辆列表
     */
    Page<Vehicle> listVehicles(int pageNum, int pageSize);

    /**
     * 更新车辆GPS位置
     * @param id 车辆ID
     * @param gpsInfo GPS位置信息
     * @return 更新后的车辆信息
     */
    Vehicle updateGpsInfo(Long id, String gpsInfo);

    /**
     * 更新出库时间
     * @param id 车辆ID
     * @param outboundTime 出库时间
     * @return 更新后的车辆信息
     */
    Vehicle updateOutboundTime(Long id, java.util.Date outboundTime);
}