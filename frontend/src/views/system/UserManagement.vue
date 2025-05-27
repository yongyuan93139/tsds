<template>
  <div class="user-management">
    <el-card>
      <template #header>
        <div class="clearfix">
          <span>用户管理</span>
          <br/>
          <el-button 
            type="primary" 
           
            @click="handleCreate"
            v-permission="['user:add']">
            新增用户
          </el-button>
        </div>
      </template>

      <el-table 
        :data="userList" 
        border 
        style="width: 100%"
        v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名"></el-table-column>
        <el-table-column prop="realName" label="真实姓名"></el-table-column>
        <el-table-column label="状态" prop="status">
          <template #default="scope">{{ scope.status === 1 ? '禁用' : '启用'}}</template>
           
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button 
              size="mini" 
              @click="handleEdit(row)"
              v-permission="['user:update']">
              编辑
            </el-button>
            <el-button 
              size="mini" 
              type="danger" 
              @click="handleDelete(row)"
              v-permission="['user:delete']">
              删除
            </el-button>
            <el-button 
              size="mini" 
              @click="handleAssignRoles(row)"
              v-permission="['user:role']">
              分配角色
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

    <!-- 用户编辑对话框 -->
    <user-edit-dialog 
      v-if="pagination.dialogVisible"
      v-model:visible="pagination.dialogVisible"
      :user="currentUser"
      @success="() => {
        console.log('用户编辑成功')
        fetchUsers()
      }">
    </user-edit-dialog>

    <!-- 角色分配对话框 -->
    <role-assign-dialog
      v-if="pagination.roleDialogVisible"
      v-model:visible="pagination.roleDialogVisible"
      
      :user="currentUser"
      @success="fetchUsers">
    </role-assign-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { getUserList, createUser, updateUser, deleteUser } from '@/api/system'
import UserEditDialog from './components/UserEditDialog.vue'
import RoleAssignDialog from './components/RoleAssignDialog.vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 确保ElMessage可以在methods中使用
const message = ElMessage

export default {
  name: 'UserManagement',
  components: { UserEditDialog, RoleAssignDialog },
  setup() {
    const loading = ref(false)
    const userList = ref([])
    const pagination = reactive({
      dialogVisible: false,
      roleDialogVisible: false,
      current: 1,
      size: 10,
      total: 0
    })
  
    // const dialogVisible = ref(false)
    // const roleDialogVisible = ref(false)
    const currentUser = ref({})

    const fetchUsers = async () => {
      try {
        loading.value = true
        console.log('正在获取用户列表...')
        
        const params = {
          page: pagination.current,
          size: pagination.size
        }
        console.log('请求参数:', params)
        
        const response = await getUserList(params)
        console.log('API响应:', response)
        
        // 处理分页响应数据
        const responseData = response.data || response
        const pageData = responseData.data || responseData
        
        userList.value = pageData.records || []
        pagination.total = pageData.total || 0
        
        console.log('用户列表数据:', userList.value)
      } catch (error) {
        console.error('获取用户列表失败:', error)
        ElMessage.error('获取用户列表失败')
      } finally {
        loading.value = false
      }
    }

    const handleEdit = (row) => {
      if (!row) {
        console.error('handleEdit: 无效的行数据', row)
        return
      }
      console.log('正在编辑用户:', JSON.parse(JSON.stringify(row)))
      currentUser.value = { ...row }
      pagination.dialogVisible = true
    }

    const handleAssignRoles = (row) => {
      if (!row) {
        console.error('handleAssignRoles: 无效的行数据', row)
        return
      }
      console.log('正在为用户分配角色:', row)
      currentUser.value = { ...row }
      pagination.roleDialogVisible = true
    }

    const handleCreate = () => {
      currentUser.value = {}
      console.log('正在创建新用户')
      // dialogVisible.value = true
      pagination.dialogVisible = true
    }

    const handleDelete = async (row) => {
      if (!row) {
        console.error('handleDelete: 无效的行数据', row)
        return
      }
      try {
        console.log('正在删除用户:', row)
        await ElMessageBox.confirm(`确认删除用户 ${row.username} 吗？`, '提示', {
          type: 'warning'
        })
        await deleteUser(row.id)
        ElMessage.success('删除成功')
        await fetchUsers()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除用户失败:', error)
          ElMessage.error(`删除用户失败: ${error.message}`)
        }
      }
    }

    const handleSizeChange = (size) => {
      pagination.size = size
      fetchUsers()
    }

    const handleCurrentChange = (current) => {
      pagination.current = current
      fetchUsers()
    }

    onMounted(() => {
      console.log('UserManagement组件创建')
      fetchUsers()
    })

    return {
      loading,
      userList,
      pagination,
      // dialogVisible,
      // roleDialogVisible,
      currentUser,
      fetchUsers,
      handleEdit,
      handleAssignRoles,
      handleCreate,
      handleDelete,
      handleSizeChange,
      handleCurrentChange
      // 其他方法也需要在这里return
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
}
</style>