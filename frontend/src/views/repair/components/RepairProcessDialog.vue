<template>
  <el-dialog
    title="处理维修工单"
    v-model="dialogVisible"
    width="600px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <div class="repair-info">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="工单号">{{ repairData?.repairOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="配件信息">{{ getFormattedPartInfo() }}</el-descriptions-item>
        <el-descriptions-item label="报障时间">{{ repairData?.faultTime }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ repairData?.creator }}</el-descriptions-item>
      </el-descriptions>
      
      <el-descriptions :column="1" border class="mt-20">
        <el-descriptions-item label="故障描述">{{ repairData?.faultDescription }}</el-descriptions-item>
      </el-descriptions>
    </div>

    <el-divider>处理信息</el-divider>

    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="process-form"
    >
      <!-- 处理结果 -->
      <el-form-item label="处理结果" prop="processResult">
        <el-input
          v-model="formData.processResult"
          type="textarea"
          :rows="3"
          placeholder="请输入处理结果"
        />
      </el-form-item>

      <!-- 故障原因 -->
      <el-form-item label="故障原因" prop="faultReason">
        <el-input
          v-model="formData.faultReason"
          type="textarea"
          :rows="3"
          placeholder="请输入故障原因"
        />
      </el-form-item>

      <!-- 处理人 -->
      <el-form-item label="处理人" prop="operator">
        <el-input
          v-model="formData.operator"
          placeholder="请输入处理人"
        />
      </el-form-item>

      <!-- 附件上传 -->
      <el-form-item label="处理附件">
        <el-upload
          ref="uploadRef"
          :action="uploadConfig.url"
          :data="uploadConfig.data"
          :on-success="handleUploadSuccess"
          :on-error="handleUploadError"
          :on-remove="handleRemove"
          :before-upload="beforeUpload"
          multiple
          :file-list="fileList"
        >
          <el-button type="primary">选择文件</el-button>
          <template #tip>
            <div class="el-upload__tip">
              支持任意格式文件，单个文件不超过10MB
            </div>
          </template>
        </el-upload>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="handleSubmit">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { processRepair } from '@/api/repair'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  repairData: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 对话框可见性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 获取格式化的配件信息
const getFormattedPartInfo = () => {
  if (!props.repairData?.partInfo) return '-'
  return props.repairData.partInfo
}

// 表单相关
const formRef = ref(null)
const submitting = ref(false)
const formData = reactive({
  processResult: '',
  faultReason: '',
  attachments: '',
  operator: ''  // 新增处理人字段
})

// 表单校验规则
const rules = {
  processResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }],
  faultReason: [{ required: true, message: '请输入故障原因', trigger: 'blur' }],
  operator: [{ required: true, message: '请输入处理人', trigger: 'blur' }]  // 新增处理人验证规则
}

// 附件上传相关
const uploadRef = ref(null)
const fileList = ref([])
const uploadConfig = {
  url: 'http://192.168.3.61/fastdfs/group1/upload',
  data: { path: 'tsds' }
}

// 上传相关方法
const beforeUpload = (file) => {
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过 10MB!')
    return false
  }
  return true
}

const handleUploadSuccess = (response, file) => {
  file.url = response.data
  ElMessage.success('文件上传成功')
}

const handleUploadError = () => {
  ElMessage.error('文件上传失败')
}

const handleRemove = (file) => {
  const index = fileList.value.indexOf(file)
  if (index !== -1) {
    fileList.value.splice(index, 1)
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    try {
      submitting.value = true
      
      // 整理附件URL
      const attachments = fileList.value
        .filter(file => file.status === 'success')
        .map(file => file.url)
        .join(',')
      
      // 提交处理结果
      await processRepair(props.repairData.id, {
        ...formData,
        attachments
      })
      
      ElMessage.success('处理成功')
      dialogVisible.value = false
      emit('success')
    } catch (error) {
      console.error('处理失败:', error)
      ElMessage.error('处理失败')
    } finally {
      submitting.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    processResult: '',
    faultReason: '',
    attachments: '',
    operator: ''  // 新增处理人字段重置
  })
  fileList.value = []
}

// 关闭对话框时重置表单
const handleClosed = () => {
  resetForm()
}
</script>

<style scoped>
.repair-info {
  margin-bottom: 20px;
}

.mt-20 {
  margin-top: 20px;
}

.process-form {
  margin-top: 20px;
}

:deep(.el-upload-list) {
  max-height: 200px;
  overflow-y: auto;
}
</style>