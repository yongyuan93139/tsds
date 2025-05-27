package com.example.inspectioncarparts.service.impl;

import com.example.inspectioncarparts.domain.entity.Permission;
import com.example.inspectioncarparts.mapper.PermissionMapper;
import com.example.inspectioncarparts.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> findAll() {
        return permissionMapper.selectList(null);
    }

    @Override
    public List<Permission> findTree() {
        List<Permission> allPermissions = findAll();
        Map<Integer, Permission> permissionMap = new HashMap<>();
        
        // 构建权限树
        List<Permission> rootPermissions = new ArrayList<>();
        for (Permission permission : allPermissions) {
            permissionMap.put(permission.getId(), permission);
            if (permission.getParentId() == 0) {
                rootPermissions.add(permission);
            }
        }
        
        for (Permission permission : allPermissions) {
            if (permission.getParentId() != 0) {
                Permission parent = permissionMap.get(permission.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(permission);
                }
            }
        }
        
        return rootPermissions;
    }

    @Override
    public Permission findById(Integer id) {
        return permissionMapper.selectById(id);
    }

    @Override
    public void save(Permission permission) {
        permissionMapper.insert(permission);
    }

    @Override
    public void update(Permission permission) {
        permissionMapper.updateById(permission);
    }

    @Override
    public void delete(Integer id) {
        permissionMapper.deleteById(id);
    }
}