package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.PartType;
import com.example.inspectioncarparts.service.PartTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import com.example.inspectioncarparts.common.Page;

@RestController
@RequestMapping("/api/part-types")
@Api(tags = "配件类型管理")
public class PartTypeController {
    @Autowired
    private PartTypeService partTypeService;

    @PostMapping
    @ApiOperation("创建配件类型")
    public Result<PartType> create(@RequestBody PartType partType) {
        try {
            PartType created = partTypeService.createPartType(partType);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("更新配件类型")
    public Result<PartType> update(@PathVariable Long id, @RequestBody PartType partType) {
        partType.setId(id);
        try {
            PartType updated = partTypeService.updatePartType(partType);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除配件类型")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            partTypeService.deletePartType(id);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ApiOperation("获取配件类型详情")
    public Result<PartType> getById(@PathVariable Long id) {
        PartType partType = partTypeService.getPartTypeById(id);
        if (partType == null) {
            return Result.fail("配件类型不存在");
        }
        return Result.success(partType);
    }

    @GetMapping
    @ApiOperation("分页查询配件类型")
    public Result<Page<PartType>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        Page<PartType> partTypes = partTypeService.listPartTypes(pageNum, pageSize);
        return Result.success(partTypes);
    }

    @PostMapping("/import")
    @ApiOperation("批量导入配件类型")
    public Result<Boolean> importPartTypes(@RequestParam("file") MultipartFile file) {
        try {
            boolean result = partTypeService.importPartTypes(file);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }
}