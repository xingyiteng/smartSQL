-- 创建demo表
create table if not exists demo
(
    id          bigint auto_increment comment 'id' primary key,
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    is_delete   tinyint  default 0                 not null comment '是否删除 0 - 未删除 1 - 已删除'
) comment 'demo';