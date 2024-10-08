package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2023-12-30 16:02
 */
@Data
public class UserRegisterDTO implements Serializable {
    private static final long serialVersionUID = -3618290321968439804L;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    private String rePassword;
}
