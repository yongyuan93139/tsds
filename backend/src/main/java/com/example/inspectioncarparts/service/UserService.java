package com.example.inspectioncarparts.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.domain.entity.Role;
import com.example.inspectioncarparts.domain.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    // 认证相关方法
    SysUser login(String username, String password);
    void logout();
    SysUser getCurrentUser();
    
    // 用户管理方法
    List<SysUser> findAll();
    SysUser findById(Integer id);
    void save(SysUser user);
    void update(SysUser user);
    void delete(Integer id);
    
    // 角色管理方法
    List<Role> findUserRoles(Integer userId);
    void assignRoles(Integer userId, List<Integer> roleIds);

    Page<SysUser> page(Page<SysUser> page);
}