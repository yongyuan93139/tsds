package com.example.inspectioncarparts.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.inspectioncarparts.domain.entity.SysUser;
import com.example.inspectioncarparts.mapper.SysUserMapper;
import com.example.inspectioncarparts.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private SysUserMapper userMapper;
    
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
        return user;
    }
}