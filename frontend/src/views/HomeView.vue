<template>
  <div class="home-layout">
    <!-- 顶部导航菜单 -->
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

    <div class="main-content">
      <h1>欢迎使用探伤车配件管理系统</h1>
      <!-- 可以添加其他首页内容 -->
    </div>
  </div>
</template>

<script setup>
import { Menu, Setting } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

.main-content {
  flex: 1;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 菜单样式 */
.el-menu {
  border-right: none;
}
</style>