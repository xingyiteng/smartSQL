package com.iteng.startup.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author iteng
 * @date 2024-02-07 20:18
 */
@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
public class EmailProperty {

    /**
     * 发送人邮箱
     */
    private String username;
}
