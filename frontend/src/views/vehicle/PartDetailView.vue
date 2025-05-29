<template>
  <div class="part-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>配件详情</span>
        </div>
      </template>
      
      <div v-if="loading" style="text-align: center">
        <el-icon class="is-loading"><Loading /></el-icon>
        加载中...
      </div>
      
      <div v-else>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="配件编码">{{ part.partCode }}</el-descriptions-item>
          <el-descriptions-item label="配件类型">
            <template #default="scope">
              {{ getTypeName(part.typeId) }}
            </template>
          </el-descriptions-item>
          <el-descriptions-item label="领用日期"  >  {{ formatDate(part.productionDate) }}</el-descriptions-item>
         
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(part.status)">
              {{ getStatusText(part.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPartByCode } from '@/api/part'
import { getPartTypes } from '@/api/partType'


const route = useRoute()
const part = ref({})
const loading = ref(true)
const partTypes = ref([])

// 初始化加载数据
onMounted(() => {
  loadData()
})

// 加载数据
const loadData = async () => {
  try {
    const [typesRes] = await Promise.all([      
      getPartTypes({
        pageNum: 1,
        pageSize: 1000
      })
    ])  
    
    partTypes.value = typesRes.records
  } catch (error) {
      ElMessage.error(error.message || '获取数据失败')
  }
}

// 修改getTypeName方法，添加空值检查
const getTypeName = (typeId) => {
  console.log('partTypes:', partTypes.value)
  console.log('typeId:', typeId)
  if (!typeId || !partTypes.value) return '未知类型'
  const type = partTypes.value.find(t => t.id === typeId)
  return type ? type.typeName : '未知类型'
}

const statusMap = {
0: { text: '禁用', type: 'danger' },
1: { text: '可用', type: 'success' },
2: { text: '维修中', type: 'warning' },
3: { text: '报废', type: 'info' }
}

// 获取状态文本
const getStatusText = (status) => {
  return statusMap[status]?.text || '未知状态'
}

// 获取状态标签类型
const getStatusType = (status) => {
  return statusMap[status]?.type || ''
}

// 格式化日期显示
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dateString.split('T')[0]
}

onMounted(async () => {
  try {
    const res = await getPartByCode(route.params.partCode)
    part.value = res
  } catch (error) {
    ElMessage.error('获取配件详情失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.part-detail {
  padding: 20px;
}
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>