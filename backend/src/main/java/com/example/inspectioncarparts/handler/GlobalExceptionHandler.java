package com.example.inspectioncarparts.handler;

import com.example.inspectioncarparts.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        logger.error("Request URL: {}, Exception: ", request.getRequestURL(), e);
        return Result.fail("系统异常，请稍后再试");
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleThrowable(Throwable e, HttpServletRequest request) {
        logger.error("Request URL: {}, Exception: ", request.getRequestURL(), e);
        return Result.fail("系统异常，请稍后再试");
    }


    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleRuntimeException(RuntimeException e) {
        logger.error("handleRuntimeException Exception: ",  e);
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleBindException(BindException e) {
        logger.error("handleBindException, Exception: ",  e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.fail(message);
    }
}