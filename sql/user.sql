-- 新建数据库
create database if not exists my_db;

-- 切换库
use my_db;

-- 创建用户表
create table if not exists user
(
    user_id       bigint auto_increment comment '用户id' primary key,
    user_account  varchar(50)                        null comment '登录账号',
    user_password varchar(512)                       null comment '登录密码',
    username      varchar(50)                        null comment '用户名',
    gender        tinyint  default 2                 null comment '性别 0 - 男 1 - 女 2 - 未知',
    phone         varchar(128)                       null comment '手机号',
    avatar_url    varchar(1024)                      null comment '用户头像',
    user_role     tinyint  default 0                 not null comment '用户角色 0 - 普通用户 1 - 管理员',
    user_status   tinyint  default 0                 not null comment '用户状态 0 - 正常    1 - 禁用',
    create_time   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete     tinyint  default 0                 not null comment '是否删除 0 - 未删除 1 - 已删除'
) comment '用户';