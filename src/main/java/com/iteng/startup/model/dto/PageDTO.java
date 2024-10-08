package com.iteng.startup.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-03 20:36
 */
@Data
public class PageDTO implements Serializable {

    private static final long serialVersionUID = -2241322622059141329L;

    /**
     * 每页显示条数
     */
    private long pageSize;

    /**
     * 第几页
     */
    private long pageNum;

}
