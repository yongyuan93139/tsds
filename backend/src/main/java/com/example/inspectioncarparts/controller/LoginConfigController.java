package com.example.inspectioncarparts.controller;

import com.example.inspectioncarparts.common.Result;
import com.example.inspectioncarparts.config.LoginConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login-config")
@Api(tags = "登录配置管理")
public class LoginConfigController {

    @Autowired
    private LoginConfig loginConfig;

    @GetMapping
    @ApiOperation("获取当前登录验证状态")
    public Result<Integer> getLoginConfig() {
        return Result.success(loginConfig.getRequireLogin());
    }

    @PutMapping
    @ApiOperation("修改登录验证状态")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateLoginConfig(@RequestParam Integer requireLogin) {
        // 验证参数
        if (requireLogin != 0 && requireLogin != 1) {
            return Result.fail("参数错误，requireLogin只能为0或1");
        }

        loginConfig.setRequireLogin(requireLogin);
        return Result.success();
    }
}
