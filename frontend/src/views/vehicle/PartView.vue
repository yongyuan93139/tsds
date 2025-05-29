<template>
  <div class="part-layout">
    <!-- 菜单栏 -->
    <el-menu
      mode="horizontal"
      :default-active="$route.path"
      background-color="#545c64"
      text-color="#fff"
      active-text-color="#ffd04b"
      router
    >
      <el-menu-item index="/">首页</el-menu-item>
      <el-menu-item index="/part-types">配件类型管理</el-menu-item>
      <el-menu-item index="/parts">配件管理</el-menu-item>
      <el-menu-item index="/vehicles">车辆管理</el-menu-item>
    </el-menu>

    <div class="part-container">
      <el-card>
        <div class="header">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增配件
          </el-button>
  </div>

        <el-table :data="tableData" border style="width: 100%">
          <el-table-column prop="partCode" label="配件编码" width="180" />
          <!-- 移除配件名称列 -->
          <el-table-column label="配件类型">
            <template #default="scope">
              {{ getTypeName(scope.row.typeId) }}
            </template>
          </el-table-column>          
          <el-table-column 
            prop="productionDate" 
            label="领用日期"
            :formatter="(row) => formatDate(row.productionDate)"
          />
  <el-table-column prop="status" label="状态" >
    <template #default="scope">
      <el-tag :type="getStatusType(scope.row.status)">
        {{ getStatusText(scope.row.status) }}
      </el-tag>
    </template>
  </el-table-column>
<el-table-column label="操作" width="300">
  <template #default="scope">
    <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
    <el-button size="small" @click="showDetail(scope.row)">详情</el-button>
    <el-button size="small" @click="generateQRCode(scope.row)">生成二维码</el-button>
    <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
  </template>
</el-table-column>

</el-table>
        <div class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </el-card>

            <!-- 在模板中添加二维码对话框 -->
      <el-dialog v-model="qrDialogVisible" title="配件二维码" width="30%">
        <div style="text-align: center">
          <img :src="qrCodeUrl" alt="配件二维码" style="max-width: 100%">
          <p style="margin-top: 20px">配件编码: {{ currentPartCode }}</p>
        </div>
        <template #footer>
          <el-button type="primary" @click="qrDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>


      <!-- 新增/编辑对话框 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item label="配件编码" prop="partCode">
            <el-input v-model="form.partCode" />
          </el-form-item>          
          <el-form-item label="配件类型" prop="typeId">
            <el-select v-model="form.typeId" placeholder="请选择配件类型">
              <el-option
                v-for="item in partTypes"
                :key="item.id"
                :label="item.typeName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="领用日期">
              <el-date-picker
              v-model="form.productionDate"
              type="date"
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="状态" prop="status" >
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option
                v-for="(value, key) in statusMap"
                :key="key"
                :label="value.text"
                :value="parseInt(key)"
              />              
            </el-select>
          </el-form-item> 
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
  </template>
</el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getParts, createPart, updatePart, deletePart } from '@/api/part'
import { getPartTypes } from '@/api/partType'

const router = useRouter()

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const partTypes = ref([])

const qrDialogVisible = ref(false)
const qrCodeUrl = ref('')
const currentPartCode = ref('')

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

const generateQRCode = async (part) => {
  try {
    currentPartCode.value = part.partCode
    if (!currentPartCode.value) {
    ElMessage.warning('配件编码为空')
    return
  }
  
    // 修改为前端路由格式
    qrCodeUrl.value = `/api/parts/qr-code/${encodeURIComponent(currentPartCode.value)}?redirect=/parts/detail/${encodeURIComponent(currentPartCode.value)}`
    qrDialogVisible.value = true
    
  } catch (error) {
    console.error('生成二维码失败:', error)
    ElMessage.error('生成二维码失败')
  }
}

const showDetail = (part) => {
  if (!part.partCode) {
    ElMessage.warning('配件编码为空')
    return
  }
  router.push(`/parts/detail/${encodeURIComponent(part.partCode)}`)
}
// 格式化日期显示
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dateString.split('T')[0]
}


// 修改getTypeName方法，添加空值检查
const getTypeName = (typeId) => {
  if (!typeId || !partTypes.value) return '未知类型'
  const type = partTypes.value.find(t => t.id === typeId)
  return type ? type.typeName : '未知类型'
}

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

// 修改表单初始值，确保所有字段都有默认值
const form = ref({
  id: null,
  partCode: '',
  typeId: '',
  productionDate: '',
  status: '1'
})

// 简化表单验证规则
const rules = ref({
  partCode: [
    { required: true, message: '请输入配件编码', trigger: 'blur' }
  ],
  typeId: [
    { required: true, message: '请选择配件类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

// 加载数据
const loadData = async () => {
  try {
    const [partsRes, typesRes] = await Promise.all([
      getParts({
        pageNum: currentPage.value,
        pageSize: pageSize.value
      }),
      getPartTypes({
        pageNum: 1,
        pageSize: 1000
      })
    ])
    
    tableData.value = partsRes.records
    total.value = partsRes.total
    partTypes.value = typesRes.records
  } catch (error) {
      ElMessage.error(error.message || '获取数据失败')
  }
}

// 分页变化
const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  loadData()
    }
    
    // 新增
const handleCreate = () => {
  dialogTitle.value = '新增配件'
  form.value = {
    id: null,
    partCode: '',
    typeId: '',
    productionDate: '',
    status: '1'
  }
  // // 确保对话框显示前重置表单验证
  // nextTick(() => {
  //   if (formRef.value) {
  //     formRef.value.clearValidate()
  //   }
  // })
  dialogVisible.value = true
}

// 编辑 - 移除了partName
const handleEdit = (row) => {
  if (!row) return
  
  dialogTitle.value = '编辑配件'
  form.value = { 
    id: row.id || null,
    partCode: row.partCode || '',
    typeId: row.typeId || '',
    productionDate: row.productionDate || '',
    status: row.status || '1'
  }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该配件吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePart(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    if (form.value.id) {
      await updatePart(form.value)
      ElMessage.success('更新成功')
    } else {
      await createPart(form.value)
      ElMessage.success('新增成功')
    }
    
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

// 初始化加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
.part-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.part-container {
  padding: 20px;
  flex: 1;
  overflow: auto;
}

.header {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 菜单样式 */
.el-menu {
  border-right: none;
}
</style>