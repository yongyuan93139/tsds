<template>
  <el-dialog
    title="分配角色"
    v-if="detail.detailVisible"
    v-model = "detail.detailVisible"
    width="500px"
    @close="handleClose">
    <el-transfer
      v-model="assignedRoles"
      :data="allRoles"
      :titles="['可选角色', '已选角色']"
      :props="{key: 'id', label: 'name'}"
      filterable>
    </el-transfer>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRoleList, getUserRoles, assignUserRoles } from '@/api/system'

// 接收父组件的数据
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'success'])

const detail = reactive({
  detailVisible: false,
  headtxt: '角色权限分配'
})

const allRoles = ref([])
const assignedRoles = ref([])

const fetchData = async () => {
  try {
    const [rolesRes, userRolesRes] = await Promise.all([
      getRoleList(),
      getUserRoles(props.user.id)
    ])
    allRoles.value = rolesRes
    assignedRoles.value = userRolesRes.map(role => role.id)
  } catch (error) {
    console.error(error)
  }
}

const handleClose = () => {
  allRoles.value = []
  assignedRoles.value = []
  detail.detailVisible = false
  emit('update:visible', false)
}

const handleSubmit = async () => {
  try {
    await assignUserRoles(props.user.id, assignedRoles.value)
    ElMessage.success('角色分配成功')
    emit('success')
    detail.detailVisible = false
    emit('update:visible', false)
  } catch (error) {
    console.error(error)
    ElMessage.error('角色分配失败')
  }
}

// watch(() => props.visible, (val) => {
//   alert('RoleAssignDialog visible changed')
//   console.log('RoleAssignDialog visible changed:', val)
//   // 仅在对话框打开时获取数据
//   if (val) {
//     fetchData()
//   }
// })
onMounted(() => {
   detail.detailVisible = props.visible;
   if (detail.detailVisible) {
    fetchData()
    }
  
})


</script>