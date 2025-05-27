package com.example.inspectioncarparts.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.inspectioncarparts.domain.entity.Permission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission> {
    
    @Select("SELECT p.* FROM permission p " +
           "JOIN role_permission rp ON p.id = rp.permission_id " +
           "WHERE rp.role_id = #{roleId}")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Integer roleId);
    
    @Select("SELECT DISTINCT p.* FROM permission p " +
           "JOIN role_permission rp ON p.id = rp.permission_id " +
           "JOIN user_role ur ON rp.role_id = ur.role_id " +
           "WHERE ur.user_id = #{userId}")
    List<Permission> findPermissionsByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT * FROM permission WHERE parent_id = #{parentId} ORDER BY id")
    List<Permission> findByParentId(@Param("parentId") Integer parentId);
    
    @Delete("DELETE FROM role_permission WHERE role_id = #{roleId}")
    void deleteRolePermissions(@Param("roleId") Integer roleId);
    
    @Insert("<script>" +
           "INSERT INTO role_permission (role_id, permission_id) VALUES " +
           "<foreach collection='permissionIds' item='permissionId' separator=','>" +
           "(#{roleId}, #{permissionId})" +
           "</foreach>" +
           "</script>")
    void batchInsertRolePermissions(@Param("roleId") Integer roleId, 
                                  @Param("permissionIds") List<Integer> permissionIds);
}