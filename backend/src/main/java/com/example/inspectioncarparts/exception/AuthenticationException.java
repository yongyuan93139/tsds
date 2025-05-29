package com.example.inspectioncarparts.exception;

/**
 * 认证异常类
 */
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public AuthenticationException() {
    }

    public AuthenticationException(String message) {
        this.message = message;
        this.code = 401;
    }

    public AuthenticationException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public AuthenticationException setMessage(String message) {
        this.message = message;
        return this;
    }

    public AuthenticationException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }
}
