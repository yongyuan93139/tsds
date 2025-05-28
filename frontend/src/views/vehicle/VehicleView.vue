<template>
  <div class="vehicle-layout">
    <!-- 修改菜单栏部分 -->
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
  <!-- 添加登出功能 -->
  <el-submenu index="user" style="float:right">
    <template #title>
      <el-avatar size="small" style="margin-right:8px"></el-avatar>
      {{ userStore.userInfo?.username || '用户' }}
    </template>
    <el-menu-item index="logout" @click="handleLogout">退出登录</el-menu-item>
  </el-submenu>
</el-menu>

    <div class="vehicle-container">
      <el-card>
        <div class="header">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增车辆
          </el-button>
        </div>

        <el-table :data="tableData" border style="width: 100%">
          <el-table-column prop="vehicleCode" label="车辆编码" width="180" />
          <el-table-column prop="vehicleName" label="车辆名称" width="180" />
          <el-table-column prop="gpsInfo" label="GPS信息" />
          <el-table-column prop="outboundTime" label="出库时间" />
          <el-table-column label="操作" width="280">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button size="small" @click="showPartDialog(scope.row)">关联配件</el-button>
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

      <!-- 新增/编辑对话框 -->
      <el-dialog v-model="dialogVisible" :title="dialogTitle">
        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item label="车辆编码" prop="vehicleCode">
            <el-input v-model="form.vehicleCode" />
          </el-form-item>
          <el-form-item label="车辆名称" prop="vehicleName">
            <el-input v-model="form.vehicleName" />
          </el-form-item>
          <el-form-item label="GPS信息" prop="gpsInfo">
            <el-input v-model="form.gpsInfo" />
          </el-form-item>
          <el-form-item label="出库时间" prop="outboundTime">
            <el-date-picker
              v-model="form.outboundTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </template>
      </el-dialog>

      <!-- 关联配件对话框 -->
<el-dialog v-model="partDialogVisible" title="关联配件到车辆" width="70%">
  <el-collapse v-model="activeParts">
<el-collapse-item 
  v-for="type in partTypes" 
  :key="type.id" 
  :title="`${type.typeName} (${type.parts?.length || 0})`" 
  :name="type.id"
>
  <el-checkbox-group v-model="selectedParts">
    <template v-if="type.parts && type.parts.length">
      <div v-for="part in type.parts" :key="part.id" class="part-item">        
        <el-checkbox 
          :label="part.id" 
          :disabled="part.vehicleId !== null && part.vehicleId !== currentVehicle.id"
        >
          {{ part.partCode }}         
          
          <el-tag v-if="part.vehicleId !== null && part.vehicleId !== currentVehicle.id" size="small" type="info">            
            已关联
          </el-tag>
        </el-checkbox>
      </div>
    </template>
    <div v-else class="empty-parts">
      暂无配件数据
    </div>
  </el-checkbox-group>
</el-collapse-item>

<!-- 修改showPartDialog方法中的配件数据处理 -->
 </el-collapse>
  <template #footer>
    <el-button @click="partDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="associateParts">确定</el-button>
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
import { 
  getVehicles, 
  createVehicle, 
  updateVehicle, 
  deleteVehicle 
} from '@/api/vehicle'
import { getPartTypes } from '@/api/partType'
import { getPartsByVehicleAndType, batchAssociateVehicle } from '@/api/part'
// 在script setup部分修改
import { logout } from '@/api/auth' // 添加导入
// 确保导入userStore
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

// 表格数据
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框相关
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = ref({
  id: null,
  vehicleCode: '',
  vehicleName: '',
  gpsInfo: '',
  outboundTime: ''
})

// 关联配件相关
const partDialogVisible = ref(false)
const activeParts = ref([])
const selectedParts = ref([])
const partTypes = ref([])
const currentVehicle = ref(null)

// 表单验证规则
const rules = ref({
  vehicleCode: [
    { required: true, message: '请输入车辆编码', trigger: 'blur' }
  ],
  vehicleName: [
    { required: true, message: '请输入车辆名称', trigger: 'blur' }
  ]
})

// 加载数据
const loadData = async () => {
  try {
    const res = await getVehicles({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    tableData.value = res.records
    total.value = res.total
  } catch (error) {
    ElMessage.error(error.message || '获取数据失败')
  }
}

// 显示配件选择对话框
const showPartDialog = async (vehicle) => {
  currentVehicle.value = vehicle
  selectedParts.value = [] // 先清空
  partDialogVisible.value = true
  
  try {
    // 获取所有配件类型
    const typesRes = await getPartTypes({ pageNum: 1, pageSize: 1000 })
    partTypes.value = typesRes.records
    
    // 获取每个类型的配件
    for (const type of partTypes.value) {
      const partsRes = await getPartsByVehicleAndType({ 
        typeId: type.id 
      })
      console.log('配件数据:', partsRes)
      
      // 处理配件数据
      type.parts = Array.isArray(partsRes) 
        ? partsRes.map(p => ({
            ...p,
            partCode: p.partCode || '未知编码',
            partName: p.partName || '未知名称',
            vehicleId: p.vehicleId || null
          }))
        : []
      
      // 预选当前车辆已关联的配件
      type.parts.forEach(part => {
        if (part.vehicleId === currentVehicle.value.id) {
          selectedParts.value.push(part.id)
        }
      })
    }
    
    // 默认展开所有配件类型
    activeParts.value = partTypes.value.map(type => type.id)
  } catch (error) {
    console.error('获取配件错误:', error.response?.data || error.message)
    ElMessage.error('获取配件数据失败: ' + (error.response?.data?.message || error.message))
  }
}

// 关联配件到车辆
const associateParts = async () => {
  if (selectedParts.value.length === 0) {
    ElMessage.warning('请至少选择一个配件')
    return
  }

  try {
    await batchAssociateVehicle({
      vehicleId: currentVehicle.value.id,
      partIds: selectedParts.value
    })
    ElMessage.success('配件关联成功')
    partDialogVisible.value = false
  } catch (error) {
    ElMessage.error('配件关联失败: ' + error.message)
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
  dialogTitle.value = '新增车辆'
  form.value = {
    id: null,
    vehicleCode: '',
    vehicleName: '',
    gpsInfo: '',
    outboundTime: ''
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑车辆'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
ElMessageBox.confirm('确定删除该车辆吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
    await deleteVehicle(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    try {
      const logoutRes = await logout()
      console.log('登出API响应:', logoutRes)
    } catch (error) {
      console.error('登出API错误详情:', {
        message: error.message,
        stack: error.stack,
        response: error.response
      })
    }

    // 1. 清除存储
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    userStore.resetToken()

    // 2. 直接跳转不等待
    window.location.href = '/login'
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('登出错误:', error)
    }
  }
}
// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    if (form.value.id) {
      await updateVehicle(form.value)
      ElMessage.success('更新成功')
    } else {
      await createVehicle(form.value)
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
.vehicle-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.vehicle-container {
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

/* 配件项样式 */
.part-item {
  margin: 8px 0;
  padding: 4px 0;
}
</style>