<template>
  <el-dialog
    :title="repairData ? '编辑维修工单' : '新增维修工单'"
    v-model="dialogVisible"
    width="650px"
    :close-on-click-modal="false"
    @closed="handleClosed"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      label-width="100px"
      class="repair-form"
    >
      <!-- 工单号（仅编辑时显示） -->
      <el-form-item v-if="repairData" label="工单号">
        <el-input v-model="formData.repairOrderNo" disabled />
      </el-form-item>

      <!-- 配件选择 -->
      <el-form-item label="维修配件" prop="partId">
        <el-select
          v-model="formData.partId"
          placeholder="请选择配件"
          filterable
          remote
          :remote-method="handleSearchParts"
          :loading="partsLoading"
          class="full-width"
        >
          <el-option
            v-for="item in partOptions"
            :key="item.id"
            :label="`${getTypeNameById(item.typeId)}(${item.partCode})`"
            :value="item.id"
          >
            <div class="part-option">
              <!-- <span class="part-name">{{ getTypeNameById(item.typeId) }}({{ item.partCode }})</span> -->
              <div class="part-info">
                <span class="part-type">{{ getTypeNameById(item.typeId) }}</span>
                <span class="part-code">({{ item.partCode }})</span>
              </div>
            </div>
          </el-option>
        </el-select>
      </el-form-item>

      <!-- 报障时间 -->
      <el-form-item label="报障时间" prop="faultTime">
        <el-date-picker
          v-model="formData.faultTime"
          type="datetime"
          placeholder="请选择报障时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          class="full-width"
        />
      </el-form-item>

      <!-- 故障描述 -->
      <el-form-item label="故障描述" prop="faultDescription">
        <el-input
          v-model="formData.faultDescription"
          type="textarea"
          :rows="3"
          placeholder="请输入故障描述"
        />
      </el-form-item>

      <!-- 附件上传 -->
      <el-form-item label="附件">
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

      <!-- 备注 -->
      <el-form-item label="备注" prop="remark">
        <el-input
          v-model="formData.remark"
          type="textarea"
          :rows="2"
          placeholder="请输入备注信息"
        />
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
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createRepair, updateRepair } from '@/api/repair'
import { getParts } from '@/api/part'
import { getPartTypes } from '@/api/partType'

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  repairData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

// 对话框可见性
const dialogVisible = computed({
  get: () => {
    console.log('获取dialogVisible值:', props.modelValue)
    return props.modelValue
  },
  set: (val) => {
    console.log('设置dialogVisible值:', val)
    emit('update:modelValue', val)
  }
})

// 表单相关
const formRef = ref(null)
const submitting = ref(false)
const formData = reactive({
  partId: '',
  faultTime: '',
  faultDescription: '',
  attachments: '',
  remark: ''
})

// 表单校验规则
const rules = {
  partId: [{ required: true, message: '请选择维修配件', trigger: 'change' }],
  faultTime: [{ required: true, message: '请选择报障时间', trigger: 'change' }],
  faultDescription: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
}

// 配件选择相关
const partsLoading = ref(false)
const partOptions = ref([])
const partTypes = ref([])

// 根据typeId获取类型名称的方法
const getTypeNameById = (typeId) => {
  if (!partTypes.value || !typeId) return '未知类型'
  const type = partTypes.value.find(type => type.id === typeId)
  console.log('获取类型名称:', typeId, type)
  return type ? type.typeName : '未知类型'
}

// 初始化加载配件列表和配件类型
onMounted(async () => {
      try {
        partsLoading.value = true
        const [partsResponse, typesResponse] = await Promise.all([
          getParts({
            pageNum: 1,
            pageSize: 100
          }),
          getPartTypes()
        ])
        partOptions.value = partsResponse.records
        partTypes.value = typesResponse.records
        console.log('配件类型:', partTypes.value)
        console.log('配件列表:', partOptions.value)
      } catch (error) {
        console.error('加载数据失败:', error)
        ElMessage.error(error.message || '加载数据失败')
      } finally {
        partsLoading.value = false
      }
})

// 附件上传相关
const uploadRef = ref(null)
const fileList = ref([])
const uploadConfig = {
  url: 'http://192.168.3.61/fastdfs/group1/upload',
  data: { path: 'tsds' }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  Object.assign(formData, {
    partId: '',
    faultTime: '',
    faultDescription: '',
    attachments: '',
    remark: ''
  })
  fileList.value = []
}

// 关闭对话框时重置表单
const handleClosed = () => {
  resetForm()
}

// 监听编辑数据变化
watch(
  () => props.repairData,
  (newVal) => {
    if (newVal) {
      Object.assign(formData, newVal)
      // 如果有附件，转换为文件列表
      if (newVal.attachments) {
        fileList.value = newVal.attachments.split(',').map((url, index) => ({
          name: `附件${index + 1}`,
          url,
          status: 'success'
        }))
      }
    } else {
      resetForm()
    }
  },
  { immediate: true }
)

// 搜索配件
const handleSearchParts = async (query) => {
  try {
    partsLoading.value = true
    const response = await getParts({
      pageNum: 1,
      pageSize: 100,
      name: query || undefined
    })
    partOptions.value = response.records
  } catch (error) {
    console.error('搜索配件失败:', error)
    ElMessage.error(error.message || '搜索配件失败')
  } finally {
    partsLoading.value = false
  }
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
      
      const submitData = {
        ...formData,
        attachments
      }
      
      if (props.repairData) {
        // 编辑
        await updateRepair(props.repairData.id, submitData)
        ElMessage.success('修改成功')
      } else {
        // 新增
        await createRepair(submitData)
        ElMessage.success('创建成功')
      }
      
      dialogVisible.value = false
      emit('success')
    } catch (error) {
      console.error('提交失败:', error)
      ElMessage.error('操作失败')
    } finally {
      submitting.value = false
    }
  })
}

</script>

<style scoped>
.repair-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 20px;
}

.full-width {
  width: 100%;
}

.part-option {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.part-name {
  font-weight: 500;
  color: #333;
}

.part-info {
  display: flex;
  gap: 8px;
  font-size: 0.9em;
}

.part-type {
  color: #666;
}

.part-code {
  color: #909399;
}

:deep(.el-upload-list) {
  max-height: 200px;
  overflow-y: auto;
}
</style>