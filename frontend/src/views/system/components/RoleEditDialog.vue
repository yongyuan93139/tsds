<template>
  <!-- 测试用红色提示框 -->
  <div v-if="dialogVisible" style="
    position: fixed;
    top: 10px;
    left: 10px;
    background: red;
    color: white;
    padding: 10px;
    z-index: 9999;
  ">
    调试：对话框应该显示，当前dialogVisible={{dialogVisible}}
  </div>

  <!-- 原生对话框替代Element Plus对话框 -->
  <div v-if="localVisible" class="native-dialog-wrapper" @click.self="handleClose">
    <div class="native-dialog" @keydown.esc="handleClose">
      <div class="native-dialog-header">
        <h3>{{formData.id ? '编辑角色' : '新增角色'}}</h3>
        <button class="close-btn" @click="localVisible.value = false">×</button>
      </div>
      <div class="native-dialog-body">
        <el-form ref="form" :model="formData" :rules="rules" label-width="100px">
          <el-form-item label="角色名称" prop="name">
            <el-input v-model="formData.name"></el-input>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="formData.description" type="textarea"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <div class="native-dialog-footer">
        <el-button @click="localVisible.value = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </div>
  </div>

  <!-- 保留原Element Plus对话框但不显示 -->
  <!-- <el-dialog
    :title="formData.id ? '编辑角色' : '新增角色'"
    v-model="localVisible"
    width="600px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @open="() => console.log('对话框打开事件触发')"
    @close="handleClose"
    v-show="false">
  </el-dialog> -->
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted,getCurrentInstance  } from 'vue'
import { createRole, updateRole } from '@/api/system'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  role: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

// const dialogVisible = computed({
//   get: () => props.visible,
//   set: (value) => emit('update:visible', value)
// })

// 使用本地状态替代计算属性
const localVisible = ref(props.visible)

// 监听props.visible变化，更新本地状态
watch(() => props.visible, (val) => {
  console.log('props.visible变化:', val)
  localVisible.value = val
})

// 监听本地状态变化，触发事件
watch(localVisible, (val) => {
  console.log('localVisible变化:', val)
  if (val !== props.visible) {
    emit('update:visible', val)
  }
})

// 保留dialogVisible计算属性用于兼容
const dialogVisible = computed({
  get: () => localVisible.value,
  set: (val) => {
    localVisible.value = val
  }
})

const formData = reactive({
  id: '',
  name: '',
  description: ''
})

// 监听props.role变化，更新本地表单数据
watch(() => props.role, (newRole) => {
  console.group('props.role变化跟踪')
  try {
    console.log('1. 新接收的role:', JSON.parse(JSON.stringify(newRole)))
    // 深拷贝props.role到formData
    Object.keys(formData).forEach(key => {
      formData[key] = newRole[key] || ''
    })
    console.log('2. formData更新结果:', JSON.parse(JSON.stringify(formData)))
  } finally {
    console.groupEnd()
  }
}, { immediate: true, deep: true })  

const form = ref(null)

const rules = reactive({
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
})

const handleClose = (e) => {
  console.group('对话框关闭过程跟踪')
  try {
    // 阻止事件冒泡
    if (e) {
      e.stopPropagation()
      e.preventDefault()
    }
    
    console.log('1. 开始重置表单...')
    if (form.value) {
      form.value.resetFields()
      console.log('2. 表单重置完成')
    }
    
    console.log('3. 开始重置formData...')
    Object.keys(formData).forEach(key => {
      formData[key] = ''
    })
    console.log('4. formData重置完成:', JSON.parse(JSON.stringify(formData)))
    
    console.log('5. 更新本地状态')
    localVisible.value = false
    
    console.log('6. 触发update:visible事件')
    emit('update:visible', false)
  } finally {
    console.groupEnd()
  }
}

const handleSubmit = async () => {
  console.group('表单提交过程跟踪')
  try {
    console.log('1. 开始表单验证...')
    await form.value.validate()
    console.log('2. 表单验证通过')
    
    const roleData = { 
      ...formData,
      permissions: [] // 权限通过单独接口设置
    }
    
    console.log('3. 准备提交的数据:', JSON.parse(JSON.stringify(roleData)))
    
    if (roleData.id) {
      console.log('4. 执行角色更新API')
      await updateRole(roleData)
      console.log('5. 角色更新成功')
      ElMessage.success('角色更新成功')
    } else {
      console.log('4. 执行角色创建API')
      await createRole(roleData)
      console.log('5. 角色创建成功')
      ElMessage.success('角色创建成功')
    }
    
    console.log('6. 触发success事件')
    emit('success')
    
    console.log('7. 准备关闭对话框')
    // 先重置表单
    handleClose()
    
    // 直接设置本地状态
    localVisible.value = false
    console.log('8. 本地状态已更新:', localVisible.value)
    
  } catch (error) {
    console.error('保存角色失败:', error)
    if (error.response) {
      console.error('API响应错误:', error.response.data)
    }
    ElMessage.error(error.message || '保存角色失败')
  } finally {
    console.groupEnd()
  }
}


onMounted(() => {
  const instance = getCurrentInstance()
  console.log('RoleEditDialog组件已挂载')
  console.log('Element Plus版本:', instance.appContext.config.globalProperties.$ELEMENT?.version)
  console.log('注册的组件:', instance.appContext.components)
})
</script>

<style scoped>
:deep(.el-dialog) {
  z-index: 2001 !important;
}

/* 原生对话框样式 */
.native-dialog-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.native-dialog {
  width: 600px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  outline: none;
}

.native-dialog-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.native-dialog-body {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.native-dialog-footer {
  padding: 10px 20px 20px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
}

.close-btn:hover {
  color: #409EFF;
}
</style>