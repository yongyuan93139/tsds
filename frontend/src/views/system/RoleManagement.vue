<template>
  <div class="role-management">
    <el-card>
      <template #header>
        <div class="clearfix">
          <span>角色管理</span>
          <br/>
          <el-button 
            type="primary"     
            @click="handleCreate"
            v-permission="'role:update'">
            新增角色
          </el-button>
        </div>
      </template>

      <el-table :data="roleList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="角色名称"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button 
              size="small" 
              @click="(e) => {
                console.log('编辑按钮点击事件', e)
                console.log('编辑按钮被点击')
                handleEdit(scope.row)
              }"
              v-permission="['role:update']"
              style="border: 2px solid red">
              编辑
            </el-button>
            <el-button 
              size="small" 
              type="danger" 
              @click="handleDelete(scope.row)"
              v-permission="['role:delete']">
              删除
            </el-button>
            <el-button 
              size="small" 
              @click="handleAssignPermissions(scope.row)"
              v-permission="['role:permission']">
              分配权限
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="pagination.current"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pagination.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total">
      </el-pagination>
    </el-card>

    <!-- 角色编辑对话框 -->
    <role-edit-dialog    
      :visible.sync="dialogVisible"
      :role="currentRole"
      @update:visible="val => {
        console.log('收到update:visible事件:', val)
        dialogVisible = val
      }"
      @success="() => {
        console.log('收到success事件')
        fetchRoles()
      }">
    </role-edit-dialog>

    <!-- 权限分配对话框 -->
    <permission-assign-dialog
      :visible.sync="permissionDialogVisible"
      :role="currentRole"
      @update:visible="val => {
        console.log('收到权限对话框update:visible事件:', val)
        permissionDialogVisible = val
      }"
      @success="() => {
        console.log('收到权限对话框success事件')
        fetchRoles()
      }">
    </permission-assign-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { deleteRole, getRolePage } from '@/api/system'
import RoleEditDialog from './components/RoleEditDialog.vue'
import PermissionAssignDialog from './components/PermissionAssignDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const roleList = ref([])
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})
const dialogVisible = ref(false)
const permissionDialogVisible = ref(false)
const currentRole = ref({})

// 监听dialogVisible的变化
watch(dialogVisible, (newVal) => {
  console.log('dialogVisible changed:', newVal)
})

const fetchRoles = async () => {
  try {
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    const res = await getRolePage(params)
    roleList.value = res.records
    pagination.total = res.total
  } catch (error) {
    console.error('获取角色列表失败:', error)
    ElMessage.error('获取角色列表失败')
  }
}

const handleDelete = async (role) => {
  try {
    await ElMessageBox.confirm(`确定删除角色 "${role.name}" 吗?`, '提示', {
      type: 'warning'
    })
    await deleteRole(role.id)
    ElMessage.success('角色删除成功')
    fetchRoles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除角色失败:', error)
      ElMessage.error(error.message || '删除角色失败')
    }
  }
}

const handleCreate = () => {
  console.log('创建角色')
  currentRole.value = {}
  dialogVisible.value = true
}

const handleEdit = (role) => {
  console.group('handleEdit调试信息')
  try {
    console.log('1. 开始编辑角色:', JSON.parse(JSON.stringify(role)))
    currentRole.value = { ...role }
    console.log('2. currentRole设置后:', JSON.parse(JSON.stringify(currentRole.value)))
    dialogVisible.value = true
    console.log('3. dialogVisible设置为:', dialogVisible.value)
  } finally {
    console.groupEnd()
  }
}

const handleAssignPermissions = (role) => {
  console.group('分配权限处理')
  try {
    console.log('1. 当前角色:', JSON.parse(JSON.stringify(role)))
    currentRole.value = { ...role }
    console.log('2. currentRole设置后:', JSON.parse(JSON.stringify(currentRole.value)))
    
    permissionDialogVisible.value = true
    console.log('3. permissionDialogVisible设置为:', permissionDialogVisible.value)
  } finally {
    console.groupEnd()
  }
}

// 监听权限对话框状态变化
watch(permissionDialogVisible, (val) => {
  console.log('permissionDialogVisible状态变化:', val)
  if (val) {
    console.log('当前角色数据:', JSON.parse(JSON.stringify(currentRole.value)))
  }
})

const handleSizeChange = (size) => {
  pagination.size = size
  fetchRoles()
}

const handleCurrentChange = (current) => {
  pagination.current = current
  fetchRoles()
}

onMounted(() => {
  fetchRoles()
})
</script>

<style scoped>
.role-management {
  padding: 20px;
}

:deep(.role-edit-dialog) {
  z-index: 9999 !important;
}

:deep(.el-overlay) {
  background-color: rgba(0, 0, 0, 0.5) !important;
}
</style>