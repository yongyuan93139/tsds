import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCurrentUser } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const permissions = ref(JSON.parse(localStorage.getItem('permissions') || '[]'))

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  const setPermissions = (perms) => {
    permissions.value = perms
    localStorage.setItem('permissions', JSON.stringify(perms))
  }

  // 从登录响应中提取权限
  const extractPermissionsFromLoginResponse = (loginData) => {
    console.log('提取权限数据，原始数据:', loginData)
    
    // 处理嵌套的响应数据结构
    const userData = loginData.user || loginData
    setUserInfo(userData)
    
    // 提取权限数据 - 多种可能的格式
    let allPermissions = []
    
    // 1. 从角色中提取权限
    if (userData.roles && userData.roles.length) {
      userData.roles.forEach(role => {
        if (role.permissions) {
          allPermissions.push(...role.permissions)
        }
      })
    }
    
    // 2. 从authorities中提取权限
    if (userData.authorities && userData.authorities.length) {
      const authPermissions = userData.authorities.map(auth => ({
        id: auth.id || 0,
        code: auth.authority || auth.code,
        name: auth.name || auth.authority || auth.code,
        type: auth.type || 1
      }))
      allPermissions.push(...authPermissions)
    }
    
    // 确保权限数据格式正确
    const normalizedPermissions = allPermissions.map(p => ({
      id: p.id || 0,
      code: p.code || p.authority || '',
      name: p.name || p.code || p.authority || '',
      type: p.type || 1
    }))
    
    console.log('规范化后的权限数据:', normalizedPermissions)
    setPermissions(normalizedPermissions)
    
    return userData
  }

  const loadUserInfo = async () => {
    try {
      const { data } = await getCurrentUser()
      return extractPermissionsFromLoginResponse(data)
    } catch (error) {
      resetToken()
      throw error
    }
  }

  const resetToken = () => {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('permissions')
    // 清除路由历史记录
    if (window.router) {
      window.router.replace('/login')
    }
  }

  // 检查是否有指定权限
  const hasPermission = (permissionCode) => {
    if (!permissionCode) return true
    
    // 支持多种权限码格式
    const codes = Array.isArray(permissionCode) ? permissionCode : [permissionCode]
    
    // 检查用户是否有任一权限
    return codes.some(code => {
      // 检查精确匹配
      if (permissions.value.some(p => p.code === code)) {
        return true
      }
      
      // 检查通配符权限 (如user:*)
      if (code.includes(':')) {
        const [prefix] = code.split(':')
        return permissions.value.some(p => p.code === `${prefix}:*`)
      }
      
      // 检查系统管理权限 (特殊处理)
      if (code === 'system') {
        console.group('检查系统管理权限')
        console.log('当前用户权限:', permissions.value)
        
        // 检查是否有system权限
        const hasSystemPermission = permissions.value.some(p => p.code === 'system')
        console.log('是否有system权限:', hasSystemPermission)
        
        // 检查子菜单权限
        const systemRoute = window.router.options.routes.find(r => r.path === '/system')
        console.log('系统管理路由:', systemRoute)
        
        if (systemRoute?.children) {
          const hasChildPermission = systemRoute.children.some(child => {
            const childPermissions = child.meta?.permission || [];
            const childCodes = Array.isArray(childPermissions) ? childPermissions : [childPermissions];
            console.log('检查子菜单权限:', child.path, childCodes)
            return childCodes.some(childCode => {
              const hasPermission = permissions.value.some(p => p.code === childCode)
              console.log(`- 权限[${childCode}]检查结果:`, hasPermission)
              return hasPermission
            });
          })
          console.log('是否有子菜单权限:', hasChildPermission)
          console.groupEnd()
          return hasSystemPermission || hasChildPermission
        }
        
        console.groupEnd()
        return hasSystemPermission
      }
      
      return false
    })
  }

  // 计算属性
  const user = computed(() => userInfo.value)
  const tokenValue = computed(() => token.value)
  const permissionList = computed(() => permissions.value)
  // 获取路由配置
  const getRoutes = () => {
    const router = window.router // 使用全局路由实例
    if (!router) return []
    
    return router.options.routes.filter(route => {
      return !route.hidden && (!route.meta?.permission || 
             hasPermission(route.meta.permission))
    })
  }
  
  const routes = computed(() => getRoutes())

  return {
    token: tokenValue,
    user,
    permissions: permissionList,
    routes,
    setToken,
    setUserInfo,
    setPermissions,
    loadUserInfo,
    resetToken,
    hasPermission,
    extractPermissionsFromLoginResponse
  }
})