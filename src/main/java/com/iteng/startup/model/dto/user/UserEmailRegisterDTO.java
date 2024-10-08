package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-02-07 21:26
 */
@Data
public class UserEmailRegisterDTO implements Serializable {

    private static final long serialVersionUID = -9005119237997103361L;
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
