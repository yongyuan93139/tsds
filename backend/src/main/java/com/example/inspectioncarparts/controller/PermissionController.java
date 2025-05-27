package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "权限管理")
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @ApiOperation(value = "获取所有权限列表", notes = "返回所有权限的列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功获取权限列表"),
        @ApiResponse(code = 403, message = "无访问权限")
    })
    @GetMapping
//    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> findAll() {
        return Result.success(permissionService.findAll());
    }

    @ApiOperation(value = "获取权限树形结构", notes = "返回权限的树形结构数据")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功获取权限树"),
        @ApiResponse(code = 403, message = "无访问权限")
    })
    @GetMapping("/tree")
//    @PreAuthorize("hasAuthority('permission:view')")
    public Result<List<Permission>> findTree()  {
        return Result.success(permissionService.findTree());
    }

    @ApiOperation(value = "根据ID获取权限详情", notes = "根据权限ID获取权限详细信息")
    @ApiImplicitParam(name = "id", value = "权限ID", required = true, dataType = "int", paramType = "path")
    @ApiResponses({
        @ApiResponse(code = 200, message = "成功获取权限详情"),
        @ApiResponse(code = 403, message = "无访问权限"),
        @ApiResponse(code = 404, message = "权限不存在")
    })
    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:view')")
    public Result<Permission> findById(@PathVariable Integer id) {
        return Result.success(permissionService.findById(id));
    }

    @ApiOperation(value = "创建新权限", notes = "创建一个新的权限")
    @ApiImplicitParam(name = "permission", value = "权限信息", required = true, dataType = "Permission")
    @ApiResponses({
        @ApiResponse(code = 200, message = "权限创建成功"),
        @ApiResponse(code = 400, message = "参数无效"),
        @ApiResponse(code = 403, message = "无操作权限")
    })
    @PostMapping
//    @PreAuthorize("hasAuthority('permission:edit')")
    public Result<Void> create(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success();
    }

    @ApiOperation(value = "更新权限信息", notes = "根据权限ID更新权限信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "权限ID", required = true, dataType = "int", paramType = "path"),
        @ApiImplicitParam(name = "permission", value = "权限信息", required = true, dataType = "Permission")
    })
    @ApiResponses({
        @ApiResponse(code = 200, message = "权限更新成功"),
        @ApiResponse(code = 400, message = "参数无效"),
        @ApiResponse(code = 403, message = "无操作权限"),
        @ApiResponse(code = 404, message = "权限不存在")
    })
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:edit')")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.update(permission);
        return Result.success();
    }

    @ApiOperation(value = "删除权限", notes = "根据权限ID删除权限")
    @ApiImplicitParam(name = "id", value = "权限ID", required = true, dataType = "int", paramType = "path")
    @ApiResponses({
        @ApiResponse(code = 200, message = "权限删除成功"),
        @ApiResponse(code = 403, message = "无操作权限"),
        @ApiResponse(code = 404, message = "权限不存在")
    })
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('permission:edit')")
    public Result<Void> delete(@PathVariable Integer id) {
        permissionService.delete(id);
        return Result.success();
    }
}