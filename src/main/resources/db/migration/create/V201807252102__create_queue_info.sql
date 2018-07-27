CREATE TABLE If NOT EXISTS `queue_info`(
   `id` int auto_increment NOT NULL COMMENT '主键id',
   `queue_name` CHAR(128) NOT NULL COMMENT '队列名',
   `exchange` VARCHAR(128) NOT NULL COMMENT '路由交换名',
   `routing_key` VARCHAR(128) NOT NULL COMMENT '访问键',
   `status` VARCHAR(10) NOT NULL DEFAULT 'VALID' COMMENT '记录状态(VALID,INVALID,DELETED)',
   `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
   PRIMARY KEY(`id`)
) Engine = InnoDB DEFAULT CHARSET = utf8 COMMENT '队列信息表';

