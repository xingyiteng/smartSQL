package com.iteng.startup.model.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author iteng
 * @date 2024-01-07 16:17
 */
@Component
@ConfigurationProperties(prefix = "project")
@Data
public class ProjectProperty {
    private String path;
}
