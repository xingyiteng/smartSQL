package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-02-10 23:31
 */
@Data
public class UserUpdatePasswordDTO implements Serializable {
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

    /**
     * 邮箱验证码
     */
    private String captchaText;
}
