package com.iteng.startup.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author iteng
 * @date 2023-12-30 16:37
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = -4299600783670434225L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 姓名
     */
    private String username;

    /**
     * 性别 0 - 男 1 - 女 2 - 未知
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 用户状态 0 - 正常 1 - 禁用
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}
