package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.Vehicle;
import com.example.inspectioncarparts.service.VehicleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.inspectioncarparts.common.Page;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Api(tags = "车辆管理")
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    @ApiOperation("创建车辆信息")
    public Result<Vehicle> create(@RequestBody Vehicle vehicle) {
        try {
            Vehicle created = vehicleService.createVehicle(vehicle);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新车辆信息")
    public Result<Vehicle> update(@PathVariable Long id, @RequestBody Vehicle vehicle) {
        vehicle.setId(id);
        try {
            Vehicle updated = vehicleService.updateVehicle(vehicle);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除车辆信息")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取车辆详情")
    public Result<Vehicle> getById(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle == null) {
            return Result.fail("车辆不存在");
        }
        return Result.success(vehicle);
    }

    @GetMapping
    @ApiOperation("分页查询车辆信息")
    public Result<Page<Vehicle>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<Vehicle> vehicles = vehicleService.listVehicles(pageNum, pageSize);
        return Result.success(vehicles);
    }

    @PutMapping("/{id}/gps")
    @ApiOperation("更新GPS位置")
    public Result<Vehicle> updateGps(@PathVariable Long id, @RequestParam String gpsInfo) {
        try {
            Vehicle updated = vehicleService.updateGpsInfo(id, gpsInfo);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/outbound-time")
    @ApiOperation("更新出库时间")
    public Result<Vehicle> updateOutboundTime(@PathVariable Long id, @RequestParam Date outboundTime) {
        try {
            Vehicle updated = vehicleService.updateOutboundTime(id, outboundTime);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}