package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.Vehicle;
import com.example.inspectioncarparts.service.VehicleService;
import io.swagger.annotations.*;
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
    public Result<Vehicle> update(@PathVariable Integer id, @RequestBody Vehicle vehicle) {
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
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            vehicleService.deleteVehicle(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取车辆详情")
    public Result<Vehicle> getById(@PathVariable Integer id) {
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
    @ApiOperation(value = "更新GPS位置", notes = "根据车辆ID更新其GPS位置信息，格式为经纬度坐标，如'39.9042,116.4074'")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "车辆ID", required = true, dataType = "Integer", paramType = "path"),
        @ApiImplicitParam(name = "gpsInfo", value = "GPS位置信息，格式为'纬度,经度'", required = true, dataType = "String", paramType = "query", example = "39.9042,116.4074")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "GPS位置更新成功"),
        @ApiResponse(code = 400, message = "GPS格式错误"),
        @ApiResponse(code = 404, message = "车辆不存在")
    })
    public Result<Vehicle> updateGps(@PathVariable Integer id, @RequestParam String gpsInfo) {
        try {
            Vehicle updated = vehicleService.updateGpsInfo(id, gpsInfo);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}/outbound-time")
    @ApiOperation("更新出库时间")
    public Result<Vehicle> updateOutboundTime(@PathVariable Integer id, @RequestParam Date outboundTime) {
        try {
            Vehicle updated = vehicleService.updateOutboundTime(id, outboundTime);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}