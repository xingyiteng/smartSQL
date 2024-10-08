package com.iteng.startup.model.dto.demo;

import com.iteng.startup.model.dto.PageDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-01-31 18:27
 */
@Data
public class DemoPageDTO extends PageDTO implements Serializable {
    private String username;
}
