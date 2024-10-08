package com.iteng.startup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author iteng
 * @date 2024-02-11 11:37
 */
@Data
public class UserUpdateDTO implements Serializable {

    private static final long serialVersionUID = 4045865149020156501L;

    private String username;

    private String phone;

    private Integer gender;
}
