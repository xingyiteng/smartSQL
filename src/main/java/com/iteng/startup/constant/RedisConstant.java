package com.iteng.startup.constant;

/**
 * redis常量
 * @author iteng
 * @date 2023-12-29 21:30
 */
public interface RedisConstant {

    /**
     * 登录captchaId
     */
    String STARTUP_LOGIN_CAPTCHA_ID_KEY = "startup:login:captcha:id:";

    /**
     * 登录验证码过期时间 单位：s
     */
    long STARTUP_LOGIN_CAPTCHA_ID_TTL = 120L;

    /**
     * 邮箱验证码
     */
    String STARTUP_EMAIL_CAPTCHA_ACCOUNT_KEY = "startup:email:captcha:account:";

    /**
     * 邮箱验证码过期时间 单位：s
     */
    long STARTUP_REGISTER_CAPTCHA_ACCOUNT_TTL = 120L;
}
