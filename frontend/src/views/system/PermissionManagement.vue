<template>
  <div class="permission-management">
    <el-card>
      <div slot="header" class="clearfix">
        <span>权限管理</span>
        <el-button 
          type="primary" 
          size="small" 
          @click="handleCreate"
          v-permission="'permission:edit'">
          新增权限
        </el-button>
      </div>

      <el-table
        :data="permissionList"
        row-key="id"
        border
        default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
        <el-table-column prop="name" label="权限名称"></el-table-column>
        <el-table-column prop="code" label="权限标识"></el-table-column>
        <el-table-column label="类型">
          <template slot-scope="scope">
            <el-tag :type="getTypeTag(scope.row.type)">
              {{ getTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template slot-scope="scope">
            <el-button 
              size="mini" 
              @click="handleEdit(scope.row)"
              v-permission="'permission:edit'">
              编辑
            </el-button>
            <el-button 
              size="mini" 
              type="danger" 
              @click="handleDelete(scope.row)"
              v-permission="'permission:edit'">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 权限编辑对话框 -->
    <permission-edit-dialog 
      :visible.sync="dialogVisible"
      :permission="currentPermission"
      :permission-list="permissionList"
      @success="fetchPermissions">
    </permission-edit-dialog>
  </div>
</template>

<script>
import { getPermissionTree, deletePermission } from '@/api/system'
import PermissionEditDialog from './components/PermissionEditDialog'

export default {
  name: 'PermissionManagement',
  components: { PermissionEditDialog },
  data() {
    return {
      permissionList: [],
      dialogVisible: false,
      currentPermission: {}
    }
  },
  created() {
    this.fetchPermissions()
  },
  methods: {
    async fetchPermissions() {
      const res = await getPermissionTree()
      this.permissionList = res.data
    },
    getTypeName(type) {
      const types = { 1: '菜单', 2: '接口', 3: '按钮' }
      return types[type] || '未知'
    },
    getTypeTag(type) {
      const tags = { 1: 'success', 2: 'primary', 3: 'warning' }
      return tags[type] || 'info'
    },
    handleCreate() {
      this.currentPermission = { type: 1, parentId: 0 }
      this.dialogVisible = true
    },
    handleEdit(permission) {
      this.currentPermission = { ...permission }
      this.dialogVisible = true
    },
    async handleDelete(permission) {
      try {
        await this.$confirm('确认删除该权限吗？', '提示', {
          type: 'warning'
        })
        await deletePermission(permission.id)
        this.$message.success('删除成功')
        this.fetchPermissions()
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>
.permission-management {
  padding: 20px;
}
</style>