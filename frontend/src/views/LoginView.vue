<template>
  <div class="login-container">
    <!-- 调试标记独立显示 -->
    <div v-if="debugMode" style="position: fixed; top: 10px; left: 10px; z-index: 9999">
      <el-tag type="danger">DEBUG MODE</el-tag>
      <el-button size="small" @click="toggleDebug">关闭调试</el-button>
    </div>
    
    <el-card class="login-card">
      <h2 class="login-title">探伤车配件管理系统</h2>
      <el-form 
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'

const debugMode = ref(true)
const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: 'admin',
  password: '123456'
})

const loginRules = ref({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
})

const loading = ref(false)
const loginFormRef = ref(null)

const toggleDebug = () => {
  debugMode.value = !debugMode.value
  ElMessage.info(`调试模式 ${debugMode.value ? '开启' : '关闭'}`)
}

const handleLogin = async () => {
  try {
    if (debugMode.value) {
      console.log('调试登录:', loginForm.value)
      router.push('/')
      return
    }
    
    await loginFormRef.value.validate()
    loading.value = true
    
    // 获取完整响应
    const response = await login({
      username: loginForm.value.username,
      password: loginForm.value.password
    })
    
    console.log('完整响应:', response)

    // 根据实际响应结构调整
    if (response) {
      const { token, user } = response
      if (!token) throw new Error('未获取到token')
      
      userStore.setToken(token)
      userStore.setUserInfo(user)
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      throw new Error('无效的响应数据')
    }
  } catch (error) {
    console.error('登录错误:', error)
    ElMessage.error(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('@/assets/images/login-bg.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.login-card {
  width: 400px;
  padding: 20px;
  background-color: rgba(255, 255, 255, 0.9); /* 半透明背景 */
  backdrop-filter: blur(5px); /* 磨砂玻璃效果 */
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #409eff;
}
</style>