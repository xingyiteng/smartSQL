package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2023-12-30 20:14
 */
@Data
public class UserLoginDTO implements Serializable {

    private static final long serialVersionUID = 4083745877318477879L;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;

    /**
     * 验证码
     */
    private String captchaText;

    /**
     * 验证码id
     */
    private String captchaId;
}
