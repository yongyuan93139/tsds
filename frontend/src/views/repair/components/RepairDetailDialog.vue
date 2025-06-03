<template>
  <el-dialog
    title="维修工单详情"
    v-model="dialogVisible"
    width="700px"
  >
    <el-descriptions title="基本信息" :column="2" border>
      <el-descriptions-item label="工单号">{{ repairData?.repairOrderNo }}</el-descriptions-item>
      <el-descriptions-item label="配件名称">{{ repairData?.partName }}</el-descriptions-item>
      <el-descriptions-item label="配件编号">{{ repairData?.partCode }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="repairData?.status === 0 ? 'warning' : 'success'">
          {{ repairData?.status === 0 ? '待处理' : '已处理' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建人">{{ repairData?.creator }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ repairData?.createTime }}</el-descriptions-item>
    </el-descriptions>

    <el-divider />

    <el-descriptions title="故障信息" :column="1" border>
      <el-descriptions-item label="报障时间">{{ repairData?.faultTime }}</el-descriptions-item>
      <el-descriptions-item label="故障描述">{{ repairData?.faultDescription }}</el-descriptions-item>
      <el-descriptions-item label="备注" v-if="repairData?.remark">{{ repairData?.remark }}</el-descriptions-item>
      <el-descriptions-item label="附件" v-if="repairData?.attachments">
        <div class="attachments-list">
          <div v-for="(url, index) in attachmentsList" :key="index" class="attachment-item">
            <el-link :href="url" target="_blank" type="primary">
              附件{{ index + 1 }}
              <el-icon class="el-icon--right"><Download /></el-icon>
            </el-link>
          </div>
        </div>
      </el-descriptions-item>
    </el-descriptions>

    <el-divider v-if="repairData?.status === 1" />

    <template v-if="repairData?.status === 1">
      <el-descriptions title="处理信息" :column="1" border>
        <el-descriptions-item label="处理时间">{{ repairData?.processTime }}</el-descriptions-item>
        <el-descriptions-item label="处理结果">{{ repairData?.processResult }}</el-descriptions-item>
        <el-descriptions-item label="故障原因">{{ repairData?.faultReason }}</el-descriptions-item>
        <el-descriptions-item label="处理附件" v-if="repairData?.processAttachments">
          <div class="attachments-list">
            <div v-for="(url, index) in processAttachmentsList" :key="index" class="attachment-item">
              <el-link :href="url" target="_blank" type="primary">
                附件{{ index + 1 }}
                <el-icon class="el-icon--right"><Download /></el-icon>
              </el-link>
            </div>
          </div>
        </el-descriptions-item>
      </el-descriptions>
    </template>

    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
      <el-button 
        v-if="repairData?.status === 0" 
        type="primary" 
        @click="handleProcess"
      >
        处理工单
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Download } from '@element-plus/icons-vue'

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

const emit = defineEmits(['update:modelValue', 'process'])

// 对话框可见性
const dialogVisible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

// 处理附件列表
const attachmentsList = computed(() => {
  if (!props.repairData?.attachments) return []
  return props.repairData.attachments.split(',')
})

// 处理处理附件列表
const processAttachmentsList = computed(() => {
  if (!props.repairData?.processAttachments) return []
  return props.repairData.processAttachments.split(',')
})

// 处理工单
const handleProcess = () => {
  dialogVisible.value = false
  emit('process', props.repairData)
}
</script>

<style scoped>
.attachments-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.attachment-item {
  margin-bottom: 5px;
}
</style>