package com.iteng.startup.constant;

/**
 * @author iteng
 * @date 2023-12-30 16:20
 */
public interface UserConstant {
    /**
     * 账号最小长度
     */
    int USER_ACCOUNT_MIN_LENGTH = 4;

    /**
     * 账号最大长度
     */
    int ACCOUNT_MAX_LENGTH = 50;

    /**
     * 密码最小长度
     */
    int USER_PASSWORD_MIN_LENGTH = 6;

    /**
     * 登录用户id
     */
    String LOGIN_USER_ID = "loginUserId";

    /**
     * session缓存userId过期时间, 默认s
     */
    int USER_ID_EXPIRE = 86400;

    /**
     * 用户角色 管理员
     */
    int ADMIN = 1;

    /**
     * 用户角色 普通用户
     */
    int USER = 0;

    /**
     * 默认登录密码
     */
    String DEFAULT_USER_PASSWORD = "123456";

    /**
     * 默认用户名长度
     */
    int DEFAULT_USERNAME_LENGTH = 12;

    /**
     * 邮箱验证码长度
     */
    int EMAIL_CAPTCHA_LENGTH = 5;

    /**
     * 盐值
     */
    String USER_SALT = "ITENG";
}
