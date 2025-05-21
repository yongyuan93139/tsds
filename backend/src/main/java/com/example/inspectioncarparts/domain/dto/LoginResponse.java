package com.example.inspectioncarparts.domain.dto;

import com.example.inspectioncarparts.domain.entity.SysUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private SysUser user;
    private String token;
}
