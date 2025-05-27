<template>
  <el-dropdown trigger="click">
    <div class="avatar-wrapper">
      <el-avatar :size="30" :icon="User" />
      <span class="username">{{ userStore.user?.username || '用户' }}</span>
      <el-icon class="el-icon--right"><arrow-down /></el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="handleProfile">
          <el-icon><user /></el-icon>
          <span style="margin-left: 5px;">个人中心</span>
        </el-dropdown-item>
        <el-dropdown-item divided @click="handleLogout">
          <el-icon><switch-button /></el-icon>
          <span style="margin-left: 5px;">退出登录</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import { logout } from '@/api/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const router = useRouter()

const handleProfile = () => {
  router.push('/profile')
}

const handleLogout = async () => {
  try {
    // 确认对话框
    await ElMessageBox.confirm('确定要退出系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // 调用登出API
    await logout()
    
    // 清除用户状态
    userStore.resetToken()
    
    // 显示成功提示
    ElMessage.success('已安全退出系统')
    
    // 跳转到登录页
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') { // 不是用户取消的情况
      console.error('登出失败:', error)
      ElMessage.error('退出失败: ' + (error.message || '未知错误'))
      // 即使API调用失败也清除本地状态
      userStore.resetToken()
      router.push('/login')
    }
  }
}
</script>

<style lang="scss" scoped>
.avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;

  .username {
    margin: 0 5px;
  }
}
</style>