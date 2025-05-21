package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.domain.entity.Vehicle;
import com.example.inspectioncarparts.mapper.VehicleMapper;
import com.example.inspectioncarparts.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {
    @Autowired
    private VehicleMapper vehicleMapper;

    @Override
    @Transactional
    public Vehicle createVehicle(Vehicle vehicle) {
        // 检查车辆编号是否已存在
        if (vehicleMapper.selectCount(new QueryWrapper<Vehicle>()
                .eq("vehicle_code", vehicle.getVehicleCode())) > 0) {
            throw new RuntimeException("车辆编号已存在");
        }
        
        vehicleMapper.insert(vehicle);
        return vehicle;
    }

    @Override
    @Transactional
    public Vehicle updateVehicle(Vehicle vehicle) {
        Vehicle existing = vehicleMapper.selectById(vehicle.getId());
        if (existing == null) {
            throw new RuntimeException("车辆不存在");
        }
        
        // 检查车辆编号是否已存在（排除自己）
        if (!existing.getVehicleCode().equals(vehicle.getVehicleCode()) && 
            vehicleMapper.selectCount(new QueryWrapper<Vehicle>()
                .eq("vehicle_code", vehicle.getVehicleCode())) > 0) {
            throw new RuntimeException("车辆编号已存在");
        }
        
        vehicleMapper.updateById(vehicle);
        return vehicle;
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        // 检查是否有配件关联该车辆
        // TODO: 需要注入PartMapper检查关联配件
        
        vehicleMapper.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Vehicle> listVehicles(int pageNum, int pageSize) {
        // 创建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Vehicle> page = 
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize);
        
        // 执行分页查询
        page = vehicleMapper.selectPage(page, null);

        return com.example.inspectioncarparts.common.Page.from(page);
    }

    @Transactional
    public Vehicle updateGpsInfo(Long id, String gpsInfo) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }
        vehicle.setGpsInfo(gpsInfo);
        vehicleMapper.updateById(vehicle);
        return vehicle;
    }

    @Override
    @Transactional
    public Vehicle updateOutboundTime(Long id, Date outboundTime) {
        Vehicle vehicle = vehicleMapper.selectById(id);
        if (vehicle == null) {
            throw new RuntimeException("车辆不存在");
        }
        vehicle.setOutboundTime(outboundTime);
        vehicleMapper.updateById(vehicle);
        return vehicle;
    }
}