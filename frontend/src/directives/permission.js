import { useUserStore } from '@/stores/user'

export default {
  mounted(el, binding) {
    const { value } = binding
    const userStore = useUserStore()
    const permissions = userStore.permissions
    
    if (!value) {
      console.warn('v-permission指令缺少权限标识')
      return
    }
    
    // 支持字符串或数组格式
    const permissionCodes = Array.isArray(value) ? value : [value]
    
    if (permissionCodes.length === 0) {
      console.warn('v-permission指令权限标识不能为空')
      return
    }

    const hasPermission = permissions.some(permission => {
      return permissionCodes.includes(permission.code)
    })

    if (!hasPermission) {
      el.parentNode && el.parentNode.removeChild(el)
    }
  }
}