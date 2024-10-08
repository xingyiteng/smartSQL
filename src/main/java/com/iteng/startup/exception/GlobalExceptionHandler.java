package com.iteng.startup.exception;

import com.iteng.startup.common.ErrorCode;
import com.iteng.startup.common.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author iteng
 * @date 2023-12-29 18:26
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseResult<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResponseResult.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseResult<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResponseResult.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}