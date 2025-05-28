-- 添加车辆管理一级菜单
INSERT INTO sys_menu (id, name, url, parent_id, order_num, type, icon, create_time, update_time)
VALUES (1000, '车辆管理', '', 0, 5, 0, 'el-icon-truck', NOW(), NOW());

-- 添加车辆管理二级菜单
INSERT INTO sys_menu (id, name, url, parent_id, order_num, type, icon, create_time, update_time)
VALUES (1001, '车辆管理', '/vehicle', 1000, 1, 1, 'el-icon-truck', NOW(), NOW());

-- 添加配件类型管理二级菜单
INSERT INTO sys_menu (id, name, url, parent_id, order_num, type, icon, create_time, update_time)
VALUES (1002, '配件类型管理', '/parts-type', 1000, 2, 1, 'el-icon-collection-tag', NOW(), NOW());

-- 为管理员角色分配新菜单权限 (假设角色ID为1)
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1000);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, 1002);
