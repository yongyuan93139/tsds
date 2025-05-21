package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.domain.dto.LoginResponse;
import com.example.inspectioncarparts.domain.entity.SysUser;
import com.example.inspectioncarparts.service.UserService;
import com.example.inspectioncarparts.util.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@Api(tags = "用户认证管理")
public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<LoginResponse> login(@RequestParam String username, 
                                     @RequestParam String password) {
        try {
            SysUser user = userService.login(username, password);
            // 生成JWT令牌
            String token = jwtTokenUtil.generateToken(user);
            // 创建并返回登录响应
            LoginResponse response = new LoginResponse(user, token);
            return Result.success(response);
        } catch (RuntimeException e) {
           log.error("login failed.", e);
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout() {
        userService.logout();
        return Result.success();
    }

    @GetMapping("/current")
    @ApiOperation("获取当前用户信息")
    public Result<SysUser> getCurrentUser() {
        SysUser user = userService.getCurrentUser();
        if (user == null) {
            return Result.fail("用户未登录");
        }
        return Result.success(user);
    }
}