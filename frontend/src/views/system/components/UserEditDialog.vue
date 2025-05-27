<template>

  <el-dialog draggable v-if="detail.detailVisible" v-model="detail.detailVisible" :title="detail.headtxt" width="60%"
            center align-center :before-close="handleClose">
             <el-form ref="form" :model="user" :rules="rules" label-width="100px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="user.username" :disabled="!!user.id"></el-input>
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="user.realName"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="!user.id">
        <el-input v-model="user.password" type="password"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="user.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </div>
            </el-dialog>
</template>

<script setup>
import { ref, reactive,onMounted , defineProps, defineEmits } from 'vue'
import { createUser, updateUser } from '@/api/system'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

const form = ref(null)
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

const detail = reactive({
  detailVisible: false,
  headtxt: '用户详情'
})

const handleClose = () => {
  // form.value.resetFields()
  // emit('update:visible', false)
  detail.detailVisible = false
  emit('update:visible', false)
}

const handleSubmit = async () => {
  try {
    await form.value.validate()
    
    // 处理密码确认
    if (!props.user.id && !props.user.password) {
      throw new Error('请设置密码')
    }
    
    const userData = { ...props.user }
    if (!userData.id) {
      await createUser(userData)
      ElMessage.success('用户创建成功')
    } else {
      // 更新时不修改密码
      delete userData.password
      await updateUser(userData)
      ElMessage.success('用户更新成功')
    }
    
    emit('success')
    emit('update:visible', false)
  } catch (error) {
    console.error('保存用户失败:', error)
    ElMessage.error(error.message || '保存用户失败')
  }
}

onMounted(() => {
  detail.detailVisible = props.visible;
  // searchAlarm();
});
</script>