package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.domain.entity.Role;
import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.mapper.RoleMapper;
import com.example.inspectioncarparts.mapper.PermissionMapper;
import com.example.inspectioncarparts.service.RoleService;
import com.example.inspectioncarparts.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public Role findById(Integer id) {
        Role role = roleMapper.selectById(id);
        if (role != null) {
            role.setPermissions(permissionMapper.findPermissionsByRoleId(id));
        }
        return role;
    }

    @Override
    public void save(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void update(Role role) {
        roleMapper.updateById(role);
    }

    @Override
    public void delete(Integer id) {
        roleMapper.deleteById(id);
    }

    @Override
    public List<Permission> findRolePermissions(Integer roleId) {
        return permissionMapper.findPermissionsByRoleId(roleId);
    }

    @Override
    @Transactional
    public void assignPermissions(Integer roleId, List<Integer> permissionIds) {
        // 先删除原有权限
        permissionMapper.deleteRolePermissions(roleId);
        
        // 添加新权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            permissionMapper.batchInsertRolePermissions(roleId, permissionIds);
        }
    }
    
    @Override
    public Page<Role> page(int pageNum, int pageSize, String name) {
        // 创建分页对象
        Page<Role> page = new Page<>(pageNum, pageSize);
        
        // 构建查询条件
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            queryWrapper.like("name", name);
        }
        
        // 添加排序
        queryWrapper.orderByAsc("id");
        
        // 执行分页查询
        return roleMapper.selectPage(page, queryWrapper);
    }
}