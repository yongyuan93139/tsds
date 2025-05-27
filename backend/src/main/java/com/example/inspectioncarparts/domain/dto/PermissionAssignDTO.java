package com.example.inspectioncarparts.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 角色权限分配DTO
 */
@Data
@ApiModel(description = "角色权限分配请求")
public class PermissionAssignDTO {

    @ApiModelProperty(value = "权限ID列表", required = true, example = "[1, 2, 3]")
    private List<Integer> permissionIds;
}
