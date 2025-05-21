package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.domain.entity.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    SysUser login(String username, String password);
    void logout();
    SysUser getCurrentUser();
}