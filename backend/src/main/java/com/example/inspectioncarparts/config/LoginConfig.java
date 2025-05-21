package com.example.inspectioncarparts.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security")
public class LoginConfig {
    /**
     * 是否需要登录验证
     * 1: 需要登录验证
     * 0: 不需要登录验证
     */
    private Integer requireLogin = 1;

    public Integer getRequireLogin() {
        return requireLogin;
    }

    public void setRequireLogin(Integer requireLogin) {
        this.requireLogin = requireLogin;
    }
}
