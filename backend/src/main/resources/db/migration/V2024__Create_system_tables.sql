-- 添加真实姓名字段
ALTER TABLE sys_user ADD COLUMN `real_name` VARCHAR(50) COMMENT '真实姓名';

-- 创建角色表
CREATE TABLE `role` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) UNIQUE NOT NULL,
  `description` VARCHAR(255),
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建权限表
CREATE TABLE `permission` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `code` VARCHAR(100) UNIQUE NOT NULL COMMENT '权限标识（如user:add）',
  `name` VARCHAR(50) NOT NULL,
  `type` TINYINT COMMENT '1-菜单, 2-接口, 3-按钮',
  `parent_id` INT DEFAULT 0 COMMENT '父权限ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建用户角色关联表
CREATE TABLE `user_role` (
  `user_id` INT,
  `role_id` INT,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `sys_user`(`id`),
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`)
);

-- 创建角色权限关联表
CREATE TABLE `role_permission` (
  `role_id` INT,
  `permission_id` INT,
  PRIMARY KEY (`role_id`, `permission_id`),
  FOREIGN KEY (`role_id`) REFERENCES `role`(`id`),
  FOREIGN KEY (`permission_id`) REFERENCES `permission`(`id`)
);

-- 初始化管理员角色
INSERT INTO `role` (`name`, `description`) VALUES ('admin', '系统管理员');

-- 初始化基本权限
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`) VALUES 
('system', '系统管理', 1, 0),
('user:view', '用户查看', 2, 0),
('user:edit', '用户编辑', 2, 0),
('role:view', '角色查看', 2, 0),
('role:edit', '角色编辑', 2, 0);

-- 为管理员角色分配所有权限
INSERT INTO `role_permission` (`role_id`, `permission_id`)
SELECT 1, id FROM `permission`;