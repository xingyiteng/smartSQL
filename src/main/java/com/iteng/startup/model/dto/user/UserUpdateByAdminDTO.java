package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-06 13:11
 */
@Data
public class UserUpdateByAdminDTO implements Serializable {

    private static final long serialVersionUID = -1633380682669555504L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String username;

    /**
     * 性别 0 - 男 1 - 女 2 - 未知
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态
     */
    private Integer userStatus;
}
