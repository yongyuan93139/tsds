-- 探伤车配件管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS inspection_car_parts DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE inspection_car_parts;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(0:禁用,1:正常)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 配件类型表
CREATE TABLE IF NOT EXISTS `part_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `type_code` VARCHAR(50) NOT NULL COMMENT '配件类型编号',
  `type_name` VARCHAR(100) NOT NULL COMMENT '类型名称',
  `valid_days` INT NOT NULL COMMENT '有效期限(天)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_type_code` (`type_code`),
  UNIQUE KEY `idx_type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配件类型表';

-- 车辆表
CREATE TABLE IF NOT EXISTS `vehicle` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车辆ID',
  `vehicle_code` VARCHAR(50) NOT NULL COMMENT '车辆编号',
  `vehicle_name` VARCHAR(100) NOT NULL COMMENT '车辆名称',
  `gps_info` VARCHAR(255) COMMENT 'GPS位置信息',
  `outbound_time` DATETIME COMMENT '出库时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_vehicle_code` (`vehicle_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆表';

-- 配件表
CREATE TABLE IF NOT EXISTS `part` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配件ID',
  `part_code` VARCHAR(50) NOT NULL COMMENT '配件编号',
  `parent_id` BIGINT COMMENT '父配件ID',
  `type_id` BIGINT NOT NULL COMMENT '配件类型ID',
  `vehicle_id` BIGINT COMMENT '所属车辆ID',
  `production_time` DATETIME COMMENT '生产时间',
  `activation_time` DATETIME COMMENT '启用时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_part_code` (`part_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_vehicle_id` (`vehicle_id`),
  CONSTRAINT `fk_part_parent` FOREIGN KEY (`parent_id`) REFERENCES `part` (`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_part_type` FOREIGN KEY (`type_id`) REFERENCES `part_type` (`id`),
  CONSTRAINT `fk_part_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='配件表';

-- 初始化管理员账号
-- 管理员用户初始化SQL
INSERT INTO `sys_user` (`username`, `password`, `status`, `create_time`, `update_time`) 
VALUES (
    'admin', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMy.MYm6gG7L3HvC3xPBV2QK6bJfP6Y.3zK', 
    1, 
    NOW(), 
    NOW()
);

-- 密码说明：
-- 用户名: admin
-- 明文密码: 123456
-- 使用BCrypt加密后的密码: $2a$10$N9qo8uLOickgx2ZMRZoMy.MYm6gG7L3HvC3xPBV2QK6bJfP6Y.3zK
-- 加密强度: 10
