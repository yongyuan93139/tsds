<template>
  <div class="repair-container">
    <!-- 搜索条件区域 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="工单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="已处理" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="报障时间">
          <el-date-picker
            v-model="searchForm.timeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <div class="operation-container">
      <el-button type="primary" @click="handleAdd">新增工单</el-button>
    </div>

    <!-- 表格区域 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="tableData"
        style="width: 100%"
        border
      >
        <el-table-column prop="repairOrderNo" label="工单号" width="180" />
        <el-table-column label="配件信息" width="150" prop="partInfo">
          <template #default="{ row }">
            <span v-if="partMap[row.partId]">
              {{ getTypeNameById(partMap[row.partId].typeId) }}({{ partMap[row.partId].partCode }})
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="faultTime" label="报障时间" width="180" />
        <el-table-column prop="faultDescription" label="故障描述" width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'warning' : 'success'">
              {{ row.status === 0 ? '待处理' : '已处理' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="creator" label="创建人" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column prop="operator" label="处理人" width="120">
          <template #default="{ row }">
            <span>{{ row.operator || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="processTime" label="处理时间" width="160">
          <template #default="{ row }">
            <span>{{ row.processTime || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="processResult" label="处理结果" show-overflow-tooltip>
          <template #default="{ row }">
            <span>{{ row.processResult || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleDetail(row)">详情</el-button>
            <el-button 
              v-if="row.status === 0"
              link 
              type="primary" 
              @click="handleProcess(row)"
            >处理</el-button>
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
    </el-card>

    <!-- 新增/编辑对话框 -->
    <repair-form-dialog
      v-model="showFormDialog"
      :repair-data="currentRepair"
      @success="handleSuccess"
    />

    <!-- 处理工单对话框 -->
    <repair-process-dialog
      v-if="showProcessDialog"
      v-model="showProcessDialog"
      :repair-data="currentRepair"
      @success="handleSuccess"
    />

    <!-- 工单详情对话框 -->
    <repair-detail-dialog
      v-if="showDetailDialog"
      v-model="showDetailDialog"
      :repair-data="currentRepair"
    />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  getRepairs,
  createRepair,
  updateRepair,
  processRepair,
  getById
} from '@/api/repair'
import { getParts} from '@/api/part'
import { getPartTypes } from '@/api/partType'
import RepairFormDialog from './components/RepairFormDialog.vue'
import RepairProcessDialog from './components/RepairProcessDialog.vue'
import RepairDetailDialog from './components/RepairDetailDialog.vue'

// 搜索表单数据
const searchForm = reactive({
  status: '',
  timeRange: []
})

// 表格数据
const loading = ref(false)
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)

// 配件相关数据
const partMap = ref({})
const partTypes = ref([])

// 根据typeId获取类型名称的方法
const getTypeNameById = (typeId) => {
  if (!partTypes.value || !typeId) return '未知类型'
  const type = partTypes.value.find(type => type.id === typeId)
  return type ? type.typeName : '未知类型'
}

// 加载配件和配件类型数据
const loadPartsData = async () => {
  try {
    const [partsResponse, typesResponse] = await Promise.all([
      getParts({ pageNum: 1, pageSize: 1000 }),
      getPartTypes()
    ])
    
    // 将配件数据转换为Map形式，方便查询
    partMap.value = partsResponse.records.reduce((acc, part) => {
      acc[part.id] = part
      return acc
    }, {})
    
    partTypes.value = typesResponse.records
  } catch (error) {
    console.error('加载配件数据失败:', error)
    ElMessage.error('加载配件数据失败')
  }
}

// 对话框控制
const showFormDialog = ref(false)
const showProcessDialog = ref(false)
const showDetailDialog = ref(false)
const currentRepair = ref(null)

// 获取工单列表
const fetchRepairList = async () => {
  try {
    loading.value = true
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      status: searchForm.status,
      startTime: searchForm.timeRange?.[0],
      endTime: searchForm.timeRange?.[1]
    }
    
    const response = await getRepairs(params)
    tableData.value = response.records
    total.value = response.total
  } catch (error) {
    ElMessage.error('获取工单列表失败')
    console.error('获取工单列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchRepairList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.status = ''
  searchForm.timeRange = []
  handleSearch()
}

// 新增工单
const handleAdd = () => {
  console.log('新增工单', showFormDialog.value)
  currentRepair.value = null
  showFormDialog.value = true
  console.log('showFormDialog设置为true后:', showFormDialog.value)
}

// 编辑工单
const handleEdit = (row) => {
  currentRepair.value = { ...row }
  showFormDialog.value = true
}

// 查看详情
const handleDetail = async (row) => {
  try {
    loading.value = true
    const response = await getById(row.id)
    currentRepair.value = response
    showDetailDialog.value = true
  } catch (error) {
    ElMessage.error('获取工单详情失败')
    console.error('获取工单详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 处理工单
const handleProcess = (row) => {
  // 获取格式化后的配件信息
  const formattedPartInfo = row.partId && partMap.value[row.partId]
    ? `${getTypeNameById(partMap.value[row.partId].typeId)}(${partMap.value[row.partId].partCode})`
    : '-'
    
  currentRepair.value = { 
    ...row,
    partInfo: formattedPartInfo
  }
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
loadPartsData()
fetchRepairList()
</script>

<style scoped>
.repair-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.operation-container {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>