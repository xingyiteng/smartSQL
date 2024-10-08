package com.iteng.startup.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 封装验证码
 * @author iteng
 * @date 2023-12-29 21:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextAndBufferedImgDTO implements Serializable {

    private static final long serialVersionUID = 5310644904365568223L;
    /**
     * 验证码文本/运算结果
     */
    private String text;
    /**
     * 验证码图片流
     */
    private BufferedImage bufferedImage;
}
