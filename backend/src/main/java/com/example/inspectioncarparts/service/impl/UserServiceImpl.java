package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.inspectioncarparts.domain.entity.Role;
import com.example.inspectioncarparts.domain.entity.SysUser;
import com.example.inspectioncarparts.mapper.RoleMapper;
import com.example.inspectioncarparts.mapper.SysUserMapper;
import com.example.inspectioncarparts.service.UserService;
import org.apache.catalina.mbeans.RoleMBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    RoleMapper roleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser login(String username, String password) {
        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("账号不存在");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        if (user.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }

        // 加载用户角色和权限
        List<Role> roles = findUserRoles(user.getId());
        user.setRoles(roles);

        // 登录成功
        return user;
    }

    @Override
    public void logout() {
        // 登出逻辑由Spring Security处理
    }

    @Override
    public SysUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            return userMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        }
        return null;
    }

    /**
     * Loads user details by username for authentication.
     * 
     * @param username the username to search for
     * @return UserDetails containing user information
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userMapper.selectOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        
        // 加载用户角色和权限
        List<Role> roles = findUserRoles(user.getId());
        user.setRoles(roles);
        
        return user;
    }

    @Override
    public List<SysUser> findAll() {
        return userMapper.selectList(new QueryWrapper<>());
    }

    @Override
    public SysUser findById(Integer id) {
        SysUser user = userMapper.selectById(id);
        if (user != null) {
            user.setRoles(findUserRoles(id));
        }
        return user;
    }

    @Override
    public void save(SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userMapper.updateById(user);
    }

    @Override
    public void delete(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public List<Role> findUserRoles(Integer userId) {
        List<Role> roles = roleMapper.findRolesByUserId(userId);
        roles.forEach(role -> role.setPermissions(roleMapper.findPermissionsByRoleId(role.getId())));
        return roles;
    }

    @Override
    @Transactional
    public void assignRoles(Integer userId, List<Integer> roleIds) {
        // 先删除原有角色
        userMapper.deleteUserRoles(userId);
        
        // 添加新角色
        if (roleIds != null && !roleIds.isEmpty()) {
            userMapper.batchInsertUserRoles(userId, roleIds);
        }
    }

    @Override
    public Page<SysUser> page(Page<SysUser> page) {
        // 1. 构建查询条件
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();

        // 2. 执行分页查询
        return userMapper.selectPage(page, queryWrapper);
    }
}