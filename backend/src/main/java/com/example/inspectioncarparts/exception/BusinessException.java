package com.example.inspectioncarparts.exception;

import com.example.inspectioncarparts.common.Constants;

public class BusinessException extends RuntimeException {
    private int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(Constants.CODE_FAIL, message);
    }

    public BusinessException() {
        this(Constants.CODE_FAIL, Constants.MSG_FAIL);
    }

    public int getCode() {
        return code;
    }
}