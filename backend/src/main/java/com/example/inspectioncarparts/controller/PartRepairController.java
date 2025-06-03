package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Page;
import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.PartRepair;
import com.example.inspectioncarparts.service.PartRepairService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/part-repairs")
@Api(tags = "配件维修管理")
public class PartRepairController {

    @Autowired
    private PartRepairService partRepairService;

    @PostMapping
    @ApiOperation("创建维修工单")
    public Result<PartRepair> create(@RequestBody PartRepair partRepair) {
        PartRepair created = partRepairService.createRepair(partRepair);
        return Result.success(created);
    }

    @PutMapping("/{id}")
    @ApiOperation("更新维修工单")
    public Result<PartRepair> update(@PathVariable Long id, @RequestBody PartRepair partRepair) {
        partRepair.setId(id);
        PartRepair updated = partRepairService.updateRepair(partRepair);
        return Result.success(updated);
    }

    @GetMapping("/{id}")
    @ApiOperation("获取维修工单详情")
    public Result<PartRepair> getById(@PathVariable Long id) {
        PartRepair partRepair = partRepairService.getRepairById(id);
        if (partRepair == null) {
            return Result.fail("维修工单不存在");
        }
        return Result.success(partRepair);
    }

    @GetMapping
    @ApiOperation("分页查询维修工单")
    public Result<Page<PartRepair>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<PartRepair> partRepairs = partRepairService.listRepairs(pageNum, pageSize);
        return Result.success(partRepairs);
    }

    @PutMapping("/{id}/process")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "维修工单ID", required = true, dataType = "Long", paramType = "path"),
        @ApiImplicitParam(name = "processResult", value = "处理结果", required = true, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "faultReason", value = "故障原因", required = false, dataType = "String", paramType = "query"),
        @ApiImplicitParam(name = "operator", value = "处理人", required = true, dataType = "String", paramType = "query")
    })
    @ApiOperation("处理维修工单")
    public Result<PartRepair> processRepair(
            @PathVariable Long id,
            @RequestParam String processResult,
            @RequestParam(required = false) String faultReason,
            @RequestParam String operator) {
        PartRepair updated = partRepairService.processRepair(id, processResult, faultReason, operator);
        return Result.success(updated);
    }

    @GetMapping("/by-part/{partId}")
    @ApiOperation("根据配件ID查询维修记录")
    public Result<Page<PartRepair>> listByPartId(
            @PathVariable Integer partId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<PartRepair> partRepairs = partRepairService.listRepairsByPartId(partId, pageNum, pageSize);
        return Result.success(partRepairs);
    }
}
