package com.example.inspectioncarparts.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.domain.entity.Role;
import java.util.List;

public interface RoleService {
    List<Role> findAll();
    
    /**
     * 分页查询角色
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param name 角色名称（可选）
     * @return 分页结果
     */
    Page<Role> page(int pageNum, int pageSize, String name);
    Role findById(Integer id);
    void save(Role role);
    void update(Role role);
    void delete(Integer id);
    List<Permission> findRolePermissions(Integer roleId);
    void assignPermissions(Integer roleId, List<Integer> permissionIds);
}