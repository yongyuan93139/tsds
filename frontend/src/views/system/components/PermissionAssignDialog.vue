<template>
  <!-- 使用原生对话框替代Element Plus对话框 -->
  <div v-if="dialogVisible" class="native-dialog-wrapper" @click.self="handleClose">
    <div class="native-dialog">
      <div class="native-dialog-header">
        <h3>分配权限</h3>
        <button class="close-btn" @click="dialogVisible = false">×</button>
      </div>
      <div class="native-dialog-body">
        <el-tree
          ref="tree"
          :data="permissionTree"
          show-checkbox
          check-strictly
          node-key="id"
          :props="treeProps"
          :default-checked-keys="checkedKeys">
        </el-tree>
        <div class="tree-tip">
          提示：请选择需要分配的具体权限，父菜单会自动显示。
        </div>
      </div>
      <div class="native-dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </div>
  </div>

  <!-- 保留原Element Plus对话框但不显示 -->
  <!-- <el-dialog
    title="分配权限"
    v-model="dialogVisible"
    width="700px"
    @close="handleClose"
    v-show="false">
    <el-tree
      ref="tree"
      :data="permissionTree"
      show-checkbox
      node-key="id"
      :props="treeProps"
      :default-checked-keys="checkedKeys">
    </el-tree>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </template>
  </el-dialog> -->
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPermissionTree, getRolePermissions, assignRolePermissions } from '@/api/system'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  role: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

// 使用本地状态管理
const localVisible = ref(props.visible)

// 监听props.visible变化
watch(() => props.visible, (val) => {
  console.log('props.visible变化:', val)
  localVisible.value = val
})

// 监听本地状态变化
watch(localVisible, (val) => {
  console.log('localVisible变化:', val)
  if (val !== props.visible) {
    emit('update:visible', val)
  }
})

// 保留计算属性用于兼容
const dialogVisible = computed({
  get: () => localVisible.value,
  set: (val) => {
    localVisible.value = val
  }
})

const tree = ref(null)
const permissionTree = ref([])
const checkedKeys = ref([])
const treeProps = reactive({
  label: 'name',
  children: 'children'
})

const fetchData = async () => {
  try {
    console.group('加载权限数据')
    console.log('1. 开始加载权限数据, 角色ID:', props.role.id)
    
    const [treeRes, rolePermissionsRes] = await Promise.all([
      getPermissionTree(props.role.id), // 传递roleId参数
      getRolePermissions(props.role.id)
    ])
    
    console.log('2. 权限树数据:', treeRes)
    console.log('3. 角色已有权限:', rolePermissionsRes)
    
    // 处理数据
    const rawTree = treeRes.data || treeRes || []
    const rawPermissions = rolePermissionsRes.data || rolePermissionsRes || []
    
    console.log('4. 原始权限树:', rawTree)
    console.log('5. 原始权限列表:', rawPermissions)
    
    // 过滤权限树，只保留有权限的节点
    const filterPermissionTree = (tree, permissions) => {
      return tree.filter(node => {
        // 如果是叶子节点，检查是否在权限列表中
        if (!node.children || node.children.length === 0) {
          return permissions.some(p => p.id === node.id)
        }
        
        // 如果是父节点，递归处理子节点
        const filteredChildren = filterPermissionTree(node.children, permissions)
        node.children = filteredChildren
        
        // 只有当有子节点时才保留父节点
        return filteredChildren.length > 0
      })
    }
    
    // 设置权限树和选中的keys
    permissionTree.value = rawTree
    checkedKeys.value = rawPermissions.map(p => p.id)
    
    console.log('6. 处理后的权限树:', permissionTree.value)
    console.log('7. 选中的权限keys:', checkedKeys.value)
    
    // // 等待DOM更新后验证选中状态
    // nextTick(() => {
    //   if (tree.value) {
    //     console.log('6. DOM更新后选中的节点:', tree.value.getCheckedKeys())
    //   }
    // })
  } catch (error) {
    console.error('加载权限数据失败:', error)
    ElMessage.error('加载权限数据失败')
  } finally {
    console.groupEnd()
  }
}

const handleClose = () => {
  permissionTree.value = []
  checkedKeys.value = []
  dialogVisible.value = false
}

const handleSubmit = async () => {
  try {
    console.group('权限提交过程跟踪')
    console.log('1. tree引用:', tree.value)
    
    // 只获取完全选中的节点，不包括半选中的节点
    const checkedKeys = tree.value ? tree.value.getCheckedKeys() : []
    const halfCheckedKeys = tree.value ? tree.value.getHalfCheckedKeys() : []
    
    console.log('2. 完全选中的节点:', checkedKeys)
    console.log('3. 半选中的节点(不会提交):', halfCheckedKeys)
    
    // 只使用完全选中的节点
    const selectedKeys = [...checkedKeys]
    console.log('4. 提交的节点:', selectedKeys)
    
    if (selectedKeys.length === 0) {
      console.log('5. 没有选中任何节点')
      throw new Error('请至少选择一个权限')
    }
    
    console.log('6. 提交权限分配, 角色ID:', props.role.id)
    await assignRolePermissions(props.role.id, selectedKeys)
    console.log('7. 权限分配成功')
    
    ElMessage.success('权限分配成功')
    emit('success')
    dialogVisible.value = false
    console.log('8. 对话框已关闭')
  } catch (error) {
    console.error('分配权限失败:', error)
    ElMessage.error(error.message || '分配权限失败')
  } finally {
    console.groupEnd()
  }
}

watch(() => props.visible, (val) => {
  console.log('子组件props.visible变化:', val)
  if (val) {
    fetchData()
  }
})

onMounted(() => {
  console.log('组件挂载完成，props.visible:', props.visible)
  console.log('初始tree引用:', tree.value)
  if (props.visible) {
    fetchData()
  }
})

// 添加nextTick检查tree引用
watch(permissionTree, () => {
  console.log('权限树数据更新后，tree引用:', tree.value)
  if (tree.value) {
    console.log('当前选中的节点:', tree.value.getCheckedKeys())
    console.log('当前半选中的节点:', tree.value.getHalfCheckedKeys())
  }
})
</script>

<style scoped>
/* 原生对话框样式 */
.native-dialog-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.native-dialog {
  width: 700px;
  background: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  outline: none;
}

.native-dialog-header {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.native-dialog-body {
  padding: 20px;
  max-height: 70vh;
  overflow-y: auto;
}

.native-dialog-footer {
  padding: 10px 20px 20px;
  text-align: right;
  border-top: 1px solid #ebeef5;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #909399;
}

.close-btn:hover {
  color: #409EFF;
}

:deep(.el-tree) {
  background: transparent;
}

.tree-tip {
  margin-top: 10px;
  padding: 8px 12px;
  background-color: #f0f9eb;
  border-radius: 4px;
  border-left: 4px solid #67c23a;
  color: #67c23a;
  font-size: 12px;
}
</style>