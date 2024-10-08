package com.iteng.startup.exception;

import com.iteng.startup.common.ErrorCode;

/**
 * 自定义异常类
 * @author iteng
 * @date 2023-12-29 18:26
 */
public class BusinessException extends RuntimeException {
    private final int code;

    private final String description;

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = "";
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
