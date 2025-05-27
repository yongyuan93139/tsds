package com.example.inspectioncarparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.inspectioncarparts.domain.entity.SysUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    SysUser findByUsername(@Param("username") String username);
    
    @Delete("DELETE FROM user_role WHERE user_id = #{userId}")
    void deleteUserRoles(@Param("userId") Integer userId);
    
    @Insert("<script>" +
           "INSERT INTO user_role (user_id, role_id) VALUES " +
           "<foreach collection='roleIds' item='roleId' separator=','>" +
           "(#{userId}, #{roleId})" +
           "</foreach>" +
           "</script>")
    void batchInsertUserRoles(@Param("userId") Integer userId, 
                           @Param("roleIds") List<Integer> roleIds);
}