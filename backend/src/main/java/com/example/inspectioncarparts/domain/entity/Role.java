package com.example.inspectioncarparts.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("role")
public class Role {
    @TableId(type = IdType.AUTO) // 主键映射
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("description")
    private String description;

//    @TableField("create_time") // 数据库字段
//    private LocalDateTime createTime;
//
//    @TableField("update_time")
//    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<Permission> permissions;
}