import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import PartTypeView from '@/views/PartTypeView.vue'
import PartView from '@/views/PartView.vue'
import VehicleView from '@/views/VehicleView.vue'
import Layout from '@/layout/index.vue'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  // 系统管理 - 移到前面作为第一个主菜单
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    meta: { 
      title: '系统管理', 
      icon: 'Setting', 
      requiresAuth: true,
      permission: 'system' 
    },
    children: [
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('@/views/system/UserManagement.vue'),
        meta: { 
          title: '用户管理',
          permission: ['user:query', 'user'],
          icon: 'User'
        },
        // 添加重定向处理错误路径
        alias: ['/system/users/users']
      },
      {
        path: 'roles',
        name: 'RoleManagement',
        component: () => import('@/views/system/RoleManagement.vue'),
        meta: { 
          title: '角色管理',
          permission: ['role:query', 'role'],
          icon: 'Avatar'
        },
        // 添加重定向处理错误路径
        alias: ['/system/roles/roles']
      }
    ]
  },
  {
    path: '/part-types',
    name: 'partTypes',
    component: PartTypeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/parts',
    name: 'parts',
    component: PartView,
    meta: { requiresAuth: true }
  },
  {
    path: '/vehicles',
    name: 'vehicles',
    component: VehicleView,
    meta: { requiresAuth: true }
  },
  // 添加配件详情路由
  {
    path: '/parts/detail/:partCode',
    name: 'partDetail',
    component: () => import('@/views/PartDetailView.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

// 添加路由导航守卫调试
router.beforeEach((to, from, next) => {
  console.log('路由跳转:', from.path, '=>', to.path)
  next()
})

router.afterEach((to) => {
  console.log('当前路由组件:', to.matched.map(r => r.components?.default?.name))
})

router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = !!userStore.token

  // 检查认证状态
  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
    return
  } else if (to.name === 'login' && isAuthenticated) {
    next({ name: 'home' })
    return
  }

  // 如果已认证但用户信息未加载，则加载用户信息
  if (isAuthenticated && !userStore.user) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      await userStore.logout()
      next({ name: 'login' })
      return
    }
  }

  // 检查权限
  if (to.meta.permission) {
    const hasPermission = userStore.hasPermission(to.meta.permission)
    if (!hasPermission) {
      next({ name: 'home' })
      return
    }
  }

  next()
})

export default router