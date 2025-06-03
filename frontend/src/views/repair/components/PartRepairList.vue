<template>
  <div class="repair-list">
    <el-table
      v-loading="loading"
      :data="tableData"
      style="width: 100%"
      border
    >
      <el-table-column prop="id" label="工单号" width="180" />
      <el-table-column prop="faultTime" label="报障时间" width="180" />
      <el-table-column prop="faultDescription" label="故障描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : 'success'">
            {{ row.status === 0 ? '待处理' : '已处理' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="processTime" label="处理时间" width="180" />
      <el-table-column fixed="right" label="操作" width="120">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 详情对话框 -->
    <repair-detail-dialog
      v-if="showDetailDialog"
      v-model="showDetailDialog"
      :repair-data="currentRepair"
      @process="handleProcess"
    />

    <!-- 处理对话框 -->
    <repair-process-dialog
      v-if="showProcessDialog"
      v-model="showProcessDialog"
      :repair-data="currentRepair"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import RepairDetailDialog from './RepairDetailDialog.vue'
import RepairProcessDialog from './RepairProcessDialog.vue'

const props = defineProps({
  partId: {
    type: [Number, String],
    required: true
  }
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 对话框控制
const showDetailDialog = ref(false)
const showProcessDialog = ref(false)
const currentRepair = ref(null)

// 获取维修记录列表
const fetchRepairList = async () => {
  try {
    loading.value = true
    const response = await fetch(`/api/repairs/by-part/${props.partId}?pageNum=${currentPage.value}&pageSize=${pageSize.value}`)
    const data = await response.json()
    
    if (data.code === 200) {
      tableData.value = data.data.records
      total.value = data.data.total
    } else {
      throw new Error(data.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('获取维修记录失败')
    console.error('获取维修记录失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看详情
const handleDetail = (row) => {
  currentRepair.value = row
  showDetailDialog.value = true
}

// 处理工单
const handleProcess = (repair) => {
  currentRepair.value = repair
  showProcessDialog.value = true
}

// 操作成功回调
const handleSuccess = () => {
  fetchRepairList()
}

// 分页相关方法
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchRepairList()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchRepairList()
}

// 初始化
fetchRepairList()
</script>

<style scoped>
.repair-list {
  margin-top: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>