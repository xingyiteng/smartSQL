package com.iteng.startup.model.dto.user;

import com.iteng.startup.model.dto.PageDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-03 20:32
 */
@Data
public class UserPageDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = -5805919174225522568L;

    /**
     * 登录账号
     */
    private String userAccount;

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
