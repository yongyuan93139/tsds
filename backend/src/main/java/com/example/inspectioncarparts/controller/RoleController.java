package com.example.inspectioncarparts.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.dto.PermissionAssignDTO;
import com.example.inspectioncarparts.domain.entity.Role;
import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @ApiOperation(value = "获取所有角色列表", notes = "返回所有角色的列表")
    @GetMapping
//    @PreAuthorize("hasAuthority('role:view')")
    public Result<List<Role>> findAll() {
        return Result.success(roleService.findAll());
    }
    
    @ApiOperation(value = "分页查询角色", notes = "根据条件分页查询角色信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNum", value = "页码", defaultValue = "1", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "每页大小", defaultValue = "10", dataType = "int", paramType = "query"),
        @ApiImplicitParam(name = "name", value = "角色名称", required = false, dataType = "String", paramType = "query")
    })
    @GetMapping("/page")
//    @PreAuthorize("hasAuthority('role:view')")
    public Result<Page<Role>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String name) {
        Page<Role> page = roleService.page(pageNum, pageSize, name);
        return Result.success(page);
    }

    @ApiOperation(value = "根据ID获取角色信息", notes = "根据ID获取角色信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:view')")
    public Result<Role> findById(@PathVariable Integer id) {
        return Result.success(roleService.findById(id));
    }

    @ApiOperation(value = "创建角色", notes = "创建角色")
    @PostMapping
//    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Void> create(@RequestBody Role role) {
        roleService.save(role);
        return Result.success();
    }

    @ApiOperation(value = "更新角色", notes = "更新角色")
    @PutMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Role role) {
        role.setId(id);
        roleService.update(role);
        return Result.success();
    }

    @ApiOperation(value = "删除角色", notes = "删除角色")
    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Void> delete(@PathVariable Integer id) {
        roleService.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "获取角色拥有的权限", notes = "获取角色拥有的权限")
    @GetMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:view')")
    public Result<List<Permission>> findRolePermissions(@PathVariable Integer id) {
        return Result.success(roleService.findRolePermissions(id));
    }

    @ApiOperation(value = "为角色分配权限", notes = "为角色分配权限")
    @PostMapping("/{id}/permissions")
//    @PreAuthorize("hasAuthority('role:edit')")
    public Result<Void> assignPermissions(@PathVariable Integer id, @RequestBody PermissionAssignDTO permissionAssignDTO)  {
        roleService.assignPermissions(id, permissionAssignDTO.getPermissionIds());
        return Result.success();
    }

}