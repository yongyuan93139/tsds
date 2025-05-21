<template>
  <div class="part-type-layout">
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

    <div class="part-type-container">
      <el-card>
        <div class="header">
          <el-button type="primary" @click="handleCreate">
            <el-icon><Plus /></el-icon>
            新增配件类型
          </el-button>
        </div>

        <el-table :data="tableData" border style="width: 100%">
          <el-table-column prop="typeCode" label="类型编码" width="180" />
          <el-table-column prop="typeName" label="类型名称" width="180" />
          <el-table-column prop="validDays" label="有效期(天)" />
          <el-table-column label="操作" width="220">
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
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
          <el-form-item label="类型编码" prop="typeCode">
            <el-input v-model="form.typeCode" />
          </el-form-item>
          <el-form-item label="类型名称" prop="typeName">
            <el-input v-model="form.typeName" />
          </el-form-item>
          <el-form-item label="有效期(天)" prop="validDays">
            <el-input-number v-model="form.validDays" :min="1" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getPartTypes, createPartType, updatePartType, deletePartType } from '@/api/partType'

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
  typeCode: '',
  typeName: '',
  validDays: 30
})

// 表单验证规则
const rules = ref({
  typeCode: [
    { required: true, message: '请输入类型编码', trigger: 'blur' }
  ],
  typeName: [
    { required: true, message: '请输入类型名称', trigger: 'blur' }
  ],
  validDays: [
    { required: true, message: '请输入有效期', trigger: 'blur' }
  ]
})

// 加载数据
const loadData = async () => {
  try {
    const res = await getPartTypes({
      pageNum: currentPage.value,
      pageSize: pageSize.value
    })
    console.log("列表数据", res)
    tableData.value = res.records
    total.value = res.total
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
  dialogTitle.value = '新增配件类型'
  form.value = {
    id: null,
    typeCode: '',
    typeName: '',
    validDays: 30
  }
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑配件类型'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定删除该配件类型吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deletePartType(row.id)
    ElMessage.success('删除成功')
    loadData()
  }).catch(() => {})
}

// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    
    if (form.value.id) {
      await updatePartType(form.value)
      ElMessage.success('更新成功')
    } else {
      await createPartType(form.value)
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
.part-type-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.part-type-container {
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