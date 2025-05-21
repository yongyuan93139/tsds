-- 使用BCryptPasswordEncoder加密的密码 '123456'
-- 这个密码是通过 new BCryptPasswordEncoder().encode("123456") 生成的
-- 每次生成的结果可能不同，但都能通过 passwordEncoder.matches("123456", encodedPassword) 验证

-- 删除已存在的admin用户（如果需要）
-- DELETE FROM sys_user WHERE username = 'admin';

-- 插入新的admin用户
INSERT INTO sys_user (username, password, status, create_time)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 1, NOW());

-- 密码说明：
-- 用户名: admin
-- 明文密码: 123456
-- 如果需要更新现有用户的密码
-- UPDATE sys_user SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' WHERE username = 'admin';
