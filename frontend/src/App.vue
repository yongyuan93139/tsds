<template>
  <!-- 主路由视图 -->
  <router-view v-slot="{ Component }" v-if="showRouter">
    <layout v-if="showLayout">
      <component :is="Component" />
    </layout>
    <component v-else :is="Component" />
  </router-view>
  
  <!-- 保留测试按钮用于调试 -->
  <button 
    style="position: fixed; bottom: 20px; right: 20px; z-index: 9999" 
    @click="toggleDebug"
  >
    调试开关
  </button>
</template>

<script setup>
import { ref, computed, defineAsyncComponent } from 'vue'
import { useRoute } from 'vue-router'

const Layout = defineAsyncComponent(() => import('@/layout/index.vue'))

const route = useRoute()
const showRouter = ref(true)

// 登录页不显示布局
const showLayout = computed(() => {
  return route.name !== 'login'
})

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