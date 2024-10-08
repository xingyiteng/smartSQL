package com.iteng.startup.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-31 18:37
 */
@Data
public class Demo implements Serializable {
    private String username;
    private Long userId;
}
