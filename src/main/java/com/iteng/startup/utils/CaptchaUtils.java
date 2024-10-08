package com.iteng.startup.utils;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.iteng.startup.model.dto.TextAndBufferedImgDTO;

import java.awt.image.BufferedImage;

/**
 * 验证码工具类
 * @author iteng
 * @date 2023-12-29 20:58
 */
public class CaptchaUtils {
    public static TextAndBufferedImgDTO createCaptchaWithMath() {
        //获取对应Bean
        DefaultKaptcha captchaProducerMath = (DefaultKaptcha)SpringContextUtils.getBean("captchaProducerMath");
        String capText = captchaProducerMath.createText();
        String capStr = capText.substring(0, capText.lastIndexOf("@"));
        //运算结果
        String text = capText.substring(capText.lastIndexOf("@") + 1);
        //图片流
        BufferedImage image = captchaProducerMath.createImage(capStr);
        //封装成对象
        return new TextAndBufferedImgDTO(text, image);
    }

    public static TextAndBufferedImgDTO createCaptchaWithChar() {
        //获取对应Bean
        DefaultKaptcha captchaProducerChar = (DefaultKaptcha)SpringContextUtils.getBean("captchaProducerChar");
        //验证码
        String text = captchaProducerChar.createText();
        //图片流
        BufferedImage image = captchaProducerChar.createImage(text);
        //封装成对象
        return new TextAndBufferedImgDTO(text, image);
    }
}
