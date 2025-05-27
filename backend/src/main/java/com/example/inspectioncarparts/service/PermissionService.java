package com.example.inspectioncarparts.service;

import com.example.inspectioncarparts.domain.entity.Permission;
import java.util.List;

public interface PermissionService {
    List<Permission> findAll();
    List<Permission> findTree();
    Permission findById(Integer id);
    void save(Permission permission);
    void update(Permission permission);
    void delete(Integer id);
}