package com.example.inspectioncarparts.common;

public interface Constants {
    // 响应状态码
    int CODE_SUCCESS = 200;
    int CODE_FAIL = 500;
    int CODE_UNAUTHORIZED = 401;
    int CODE_FORBIDDEN = 403;

    // 通用消息
    String MSG_SUCCESS = "操作成功";
    String MSG_FAIL = "操作失败";
    String MSG_UNAUTHORIZED = "未授权访问";
    String MSG_FORBIDDEN = "权限不足";

    // JWT相关
    String TOKEN_HEADER = "Authorization";
    String TOKEN_PREFIX = "Bearer ";
    long TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时

    // 用户状态
    int USER_STATUS_NORMAL = 1;
    int USER_STATUS_DISABLED = 0;
}