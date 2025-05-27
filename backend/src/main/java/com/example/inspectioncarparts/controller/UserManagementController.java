package com.example.inspectioncarparts.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.dto.RoleAssignmentDTO;
import com.example.inspectioncarparts.domain.entity.Role;
import com.example.inspectioncarparts.domain.entity.SysUser;
import com.example.inspectioncarparts.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "获取用户分页列表", notes = "返回分页的用户列表")
    @GetMapping("/page")
//    @PreAuthorize("hasAuthority('user:query')")
    public Result<Page<SysUser>> getPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        return Result.success(userService.page(page));
    }

    @ApiOperation(value = "获取所有用户列表", notes = "返回所有用户的列表")
    @GetMapping
    @PreAuthorize("hasAuthority('user:query')")
    public Result<List<SysUser>> findAll() {
        return Result.success(userService.findAll());
    }

    @ApiOperation(value = "根据ID获取用户信息", notes = "根据ID获取用户信息")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:query')")
    public Result<SysUser> findById(@PathVariable Integer id) {
        return Result.success(userService.findById(id));
    }

    @ApiOperation(value = "创建用户", notes = "创建用户")
    @PostMapping
    @PreAuthorize("hasAuthority('user:add')")
    public Result<Void> create(@RequestBody SysUser user) {
        userService.save(user);
        return Result.success();
    }

    @ApiOperation(value = "更新用户", notes = "更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public Result<Void> update(@PathVariable Integer id, @RequestBody SysUser user) {
        user.setId(id);
        userService.update(user);
        return Result.success();
    }

    @ApiOperation(value = "删除用户", notes = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.delete(id);
        return Result.success();
    }

    @ApiOperation(value = "获取用户拥有的角色", notes = "获取用户拥有的角色")
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:query')")
    public Result<List<Role>> findUserRoles(@PathVariable Integer id) {
        return Result.success(userService.findUserRoles(id));
    }

    @ApiOperation(value = "为用户分配角色", notes = "为用户分配角色")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('user:role')")
    public Result assignRoles(@PathVariable Integer id, @RequestBody  RoleAssignmentDTO roleAssignment) {
        userService.assignRoles(id, roleAssignment.getRoleIds());
        return Result.success();
    }
}