package com.iteng.startup.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2023-12-29 21:42
 */
@Data
public class CaptchaVO implements Serializable {
    private static final long serialVersionUID = -5900454887007693444L;

    /**
     * 验证码id
     */
    private String captchaId;

    /**
     * 验证码图片
     */
    private String base64Image;
}
