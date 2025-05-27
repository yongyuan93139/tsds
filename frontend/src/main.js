import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import permission from '@/directives/permission'

const app = createApp(App)

// 开发环境下将路由实例挂载到全局
if (import.meta.env.DEV) {
  window.router = router
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 注册权限指令
app.directive('permission', permission)

// 注册所有Element Plus图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')