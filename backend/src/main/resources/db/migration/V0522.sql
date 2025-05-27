-- 一级菜单：系统管理
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`)
VALUES ('system', '系统管理', 1, 0);

-- 获取刚插入的系统管理ID
SET @system_id = LAST_INSERT_ID();

-- 二级菜单：用户管理
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`)
VALUES ('user', '用户管理', 1, @system_id);

SET @user_id = LAST_INSERT_ID();

-- 用户管理下的按钮权限
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`) VALUES
('user:add', '新增用户', 3, @user_id),
('user:delete', '删除用户', 3, @user_id),
('user:update', '编辑用户', 3, @user_id),
('user:query', '查询用户', 3, @user_id),
('user:role', '角色配置', 3, @user_id);

-- 二级菜单：角色管理
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`)
VALUES ('role', '角色管理', 1, @system_id);

SET @role_id = LAST_INSERT_ID();

-- 角色管理下的按钮权限
INSERT INTO `permission` (`code`, `name`, `type`, `parent_id`) VALUES
('role:add', '新增角色', 3, @role_id),
('role:delete', '删除角色', 3, @role_id),
('role:update', '编辑角色', 3, @role_id),
('role:query', '查询角色', 3, @role_id),
('role:permission', '权限配置', 3, @role_id);
