package com.iteng.startup.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author iteng
 * @date 2024-01-31 18:51
 */
@Data
public class DemoVO implements Serializable {
    private String username;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
