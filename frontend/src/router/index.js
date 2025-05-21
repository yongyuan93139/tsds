import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '@/views/LoginView.vue'
import HomeView from '@/views/HomeView.vue'
import PartTypeView from '@/views/PartTypeView.vue'
import PartView from '@/views/PartView.vue'
import VehicleView from '@/views/VehicleView.vue'
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

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = !!userStore.token

  if (to.meta.requiresAuth && !isAuthenticated) {
    next({ name: 'login' })
  } else if (to.name === 'login' && isAuthenticated) {
    next({ name: 'home' })
  } else {
    next()
  }
})

export default router