<template>
  <el-dialog
    :title="permission.id ? '编辑权限' : '新增权限'"
    :visible.sync="visible"
    width="600px"
    @close="handleClose">
    <el-form ref="form" :model="permission" :rules="rules" label-width="100px">
      <el-form-item label="权限名称" prop="name">
        <el-input v-model="permission.name"></el-input>
      </el-form-item>
      <el-form-item label="权限标识" prop="code">
        <el-input v-model="permission.code"></el-input>
      </el-form-item>
      <el-form-item label="权限类型" prop="type">
        <el-select v-model="permission.type" placeholder="请选择权限类型">
          <el-option label="菜单" :value="1"></el-option>
          <el-option label="接口" :value="2"></el-option>
          <el-option label="按钮" :value="3"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="父级权限" prop="parentId">
        <el-cascader
          v-model="parentPath"
          :options="permissionOptions"
          :props="{ checkStrictly: true, value: 'id', label: 'name' }"
          clearable>
        </el-cascader>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { createPermission, updatePermission } from '@/api/system'

export default {
  name: 'PermissionEditDialog',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    permission: {
      type: Object,
      default: () => ({ type: 1, parentId: 0 })
    },
    permissionList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      parentPath: [],
      rules: {
        name: [
          { required: true, message: '请输入权限名称', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '请输入权限标识', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '请选择权限类型', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    permissionOptions() {
      // 将权限列表转换为级联选择器需要的格式
      const buildTree = (list, parentId = 0) => {
        return list
          .filter(item => item.parentId === parentId)
          .map(item => ({
            ...item,
            children: buildTree(list, item.id)
          }))
      }
      return buildTree(this.permissionList)
    }
  },
  watch: {
    permission: {
      immediate: true,
      handler(val) {
        if (val.parentId) {
          // 设置父级路径
          const findPath = (list, id, path = []) => {
            for (const item of list) {
              if (item.id === id) {
                return [...path, item.id]
              }
              if (item.children && item.children.length) {
                const found = findPath(item.children, id, [...path, item.id])
                if (found) return found
              }
            }
            return []
          }
          this.parentPath = findPath(this.permissionOptions, val.parentId)
        } else {
          this.parentPath = []
        }
      }
    },
    parentPath(val) {
      this.permission.parentId = val.length ? val[val.length - 1] : 0
    }
  },
  methods: {
    handleClose() {
      this.$refs.form.resetFields()
      this.$emit('update:visible', false)
    },
    async handleSubmit() {
      try {
        await this.$refs.form.validate()
        if (this.permission.id) {
          await updatePermission(this.permission)
        } else {
          await createPermission(this.permission)
        }
        this.$message.success('保存成功')
        this.$emit('success')
        this.visible = false
      } catch (error) {
        console.error(error)
      }
    }
  }
}
</script>