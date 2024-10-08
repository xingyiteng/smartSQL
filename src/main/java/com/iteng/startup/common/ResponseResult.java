package com.iteng.startup.common;

import com.iteng.startup.constant.CommonConstant;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 通用返回结果
 * @author iteng
 * @date 2023-12-29 17:13
 */
@Getter
@Setter
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -7747444526680793038L;

    private int code;

    private T data;

    private String message;

    private String description;

    public ResponseResult(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public ResponseResult(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public ResponseResult(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    public ResponseResult(ErrorCode errorCode, String description) {
        this(errorCode.getCode(), null, errorCode.getMessage(), description);
    }

    public ResponseResult(ErrorCode errorCode, T data, String description) {
        this(errorCode.getCode(), data, errorCode.getMessage(), description);
    }

    /**
     * 成功 - 自定义description
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(T data, String description) {
        return new ResponseResult<>(CommonConstant.OK, data, "ok", description);
    }

    /**
     * 成功 - 无description
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(CommonConstant.OK, data, "ok");
    }

    /**
     * 成功 - 无data、自定义description
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success(String description) {
        return new ResponseResult<>(CommonConstant.OK,null, "ok", description);
    }

    /**
     * 成功 - 无data、无description
     *
     * @param <T>
     * @return
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(CommonConstant.OK,null, "ok");
    }

    /**
     * 失败 - 自定义code message description
     *
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static <T>  ResponseResult<T> error(int code, String message, String description) {
        return new ResponseResult<>(code, null, message, description);
    }

    /**
     * 失败 - 无description
     *
     * @param errorCode
     * @return
     */
    public static<T> ResponseResult<T> error(ErrorCode errorCode) {
        return new ResponseResult<>(errorCode);
    }

    /**
     * 失败 - 自定义description
     *
     * @param errorCode
     * @param description
     * @return
     */
    public static <T> ResponseResult<T> error(ErrorCode errorCode, String description) {
        return new <T> ResponseResult<T>(errorCode, description);
    }

    /**
     * 失败 - 自定义data、description
     *
     * @param errorCode
     * @param description
     * @return
     */
    public static <T> ResponseResult<T> error(ErrorCode errorCode, T data, String description) {
        return new <T> ResponseResult<T>(errorCode, data, description);
    }
}
