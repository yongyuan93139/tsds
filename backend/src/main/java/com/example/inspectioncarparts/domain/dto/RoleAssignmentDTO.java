package com.example.inspectioncarparts.domain.dto;

import java.util.List;

public class RoleAssignmentDTO {
    private List<Integer> roleIds;
    
    // Getters and setters
    public List<Integer> getRoleIds() {
        return roleIds;
    }
    
    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }
}
