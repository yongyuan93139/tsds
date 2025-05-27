<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="asideWidth" class="aside-container">
      <div class="logo-container">
        <span v-if="collapsed">TSDS</span>
        <span v-else>探伤车配件管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :unique-opened="true"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF">
        <template v-for="route in permissionRoutes" :key="route.path">
          <sidebar-item 
            v-if="!route.hidden"
            :item="route"
            :base-path="route.path" />
        </template>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="left-menu">
          <el-button
            type="text"
            @click="toggleCollapse"
            class="collapse-btn">
            <el-icon>
              <component :is="collapsed ? 'Expand' : 'Fold'" />
            </el-icon>
          </el-button>
        </div>
        <div class="right-menu">
          <user-dropdown />
        </div>
      </div>

      <!-- 内容视图 -->
      <div class="app-main">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Fold, Expand } from '@element-plus/icons-vue'
import SidebarItem from './components/SidebarItem.vue'
import UserDropdown from './components/UserDropdown.vue'

// 导入全局样式
import '@/styles/index.scss'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)
const asideWidth = computed(() => collapsed.value ? '64px' : '210px')

// 获取当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  return meta.activeMenu || path
})

// 获取有权限的路由
const permissionRoutes = computed(() => {
  // 获取所有路由
  const allRoutes = (window.router?.options?.routes || [])
    .filter(route => !route.hidden)
  
  // 调试输出路由信息
  console.log('所有路由:', allRoutes.map(r => ({
    path: r.path,
    permission: r.meta?.permission,
    hasPermission: r.meta?.permission ? 
      userStore.hasPermission(r.meta.permission) : true
  })))
  
  // 过滤有权限的路由
  const filteredRoutes = allRoutes.filter(route => {
    const requiredPermission = route.meta?.permission
    return !requiredPermission || userStore.hasPermission(requiredPermission)
  })
  
  console.log('过滤后的路由:', filteredRoutes.map(r => r.path))
  return filteredRoutes
})

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  collapsed.value = !collapsed.value
}

// 调试代码 - 检查菜单显示问题
onMounted(() => {
  console.group('系统管理菜单调试信息')
  console.log('当前用户:', userStore.user)
  console.log('用户权限列表:', userStore.permissions)
  console.log('路由配置:', router.options.routes)
  console.log('系统管理权限检查:', userStore.hasPermission('system'))
  console.log('用户管理权限检查:', userStore.hasPermission('user'))
  console.log('角色管理权限检查:', userStore.hasPermission('role'))
  console.log('过滤后的权限路由:', permissionRoutes.value)
  console.groupEnd()
})
</script>

<style lang="scss" scoped>
.layout-container {
  display: flex;
  height: 100%;

  .aside-container {
    background-color: #304156;
    transition: width 0.28s;
    height: 100%;
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1001;
    overflow: hidden;

    .logo-container {
      height: 50px;
      line-height: 50px;
      text-align: center;
      color: #fff;
      font-size: 16px;
      font-weight: 600;
      background: #2b2f3a;
      overflow: hidden;
    }
  }

  .main-container {
    flex: 1;
    margin-left: v-bind(asideWidth);
    transition: margin-left 0.28s;
    min-height: 100vh;

    .navbar {
      height: 50px;
      line-height: 50px;
      border-bottom: 1px solid #e6e6e6;
      display: flex;
      justify-content: space-between;
      padding: 0 15px;

      .collapse-btn {
        font-size: 20px;
      }
    }

    .app-main {
      min-height: calc(100vh - 50px);
      padding: 20px;
      background-color: #f0f2f5;
    }
  }
}
</style>