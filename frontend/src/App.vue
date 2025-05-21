<template>
  <!-- 恢复路由视图 -->
  <router-view v-if="showRouter"/>
  
  <!-- 保留测试按钮用于调试 -->
  <button 
    style="position: fixed; bottom: 20px; right: 20px; z-index: 9999" 
    @click="toggleDebug"
  >
    调试开关
  </button>
</template>

<script setup>
import { ref } from 'vue'

const showRouter = ref(true)

const toggleDebug = () => {
  showRouter.value = !showRouter.value
  console.log('当前路由状态:', showRouter.value ? '开启' : '关闭')
}
</script>

<style>
/* 基础样式确保全屏 */
html, body, #app {
  height: 100%;
  margin: 0;
  padding: 0;
}
</style>