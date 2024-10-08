package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-03 21:59
 */
@Data
public class UserAddDTO implements Serializable {

    private static final long serialVersionUID = -5927527129340830872L;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;
}
