package com.iteng.startup.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @author iteng
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * 用户id
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 登录账号
     */
    private String userAccount;

    /**
     * 登录密码
     */
    private String userPassword;

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
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 是否删除 0 - 未删除 1 - 已删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}