import request from '@/utils/request'

// 用户管理
export function getUserList(params) {
  return request({
    url: '/users/page',
    method: 'get',
    params: {
      current: params.page || 1,
      size: params.size || 10
    }
  })
}

export function createUser(data) {
  return request({
    url: '/users',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: `/users/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/users/${id}`,
    method: 'delete'
  })
}

export function getUserRoles(userId) {
  return request({
    url: `/users/${userId}/roles`,
    method: 'get'
  })
}

export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/users/${userId}/roles`,
    method: 'post',
    data: { roleIds }
  })
}

// 角色管理
export function getRoleList(params) {
  return request({
    url: '/roles',
    method: 'get',
    params
  })
}

// 角色分页管理
export function getRolePage(params) {
  return request({
    url: '/roles/page',
    method: 'get',
    params: {
      current: params.page || 1,
      size: params.size || 10
    }
  })
}

export function createRole(data) {
  return request({
    url: '/roles',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: `/roles/${data.id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/roles/${id}`,
    method: 'delete'
  })
}

export function getRolePermissions(roleId) {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: 'get'
  })
}

export function assignRolePermissions(roleId, permissionIds) {
  return request({
    url: `/roles/${roleId}/permissions`,
    method: 'post',
    data: { permissionIds }
  })
}

// 权限管理
export function getPermissionTree(roeId) {
  return request({
    url: '/permissions/tree',
    method: 'get'
  })
}

export function createPermission(data) {
  return request({
    url: '/permissions',
    method: 'post',
    data
  })
}

export function updatePermission(data) {
  return request({
    url: `/permissions/${data.id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/permissions/${id}`,
    method: 'delete'
  })
}