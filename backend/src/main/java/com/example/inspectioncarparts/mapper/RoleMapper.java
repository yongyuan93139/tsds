package com.example.inspectioncarparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.domain.entity.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    
    @Select("SELECT r.* FROM role r " +
           "JOIN user_role ur ON r.id = ur.role_id " +
           "WHERE ur.user_id = #{userId}")
    List<Role> findRolesByUserId(@Param("userId") Integer userId);

    @Select( "SELECT p.* FROM permission p " +
             "JOIN role_permission rp ON p.id = rp.permission_id where rp.role_id = #{id}")
    List<Permission> findPermissionsByRoleId(@Param("id") Integer id);
}