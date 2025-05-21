import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)

// 打印路由配置验证
console.log('路由实例:', router)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 明确挂载并验证
const root = document.getElementById('app')
if (root) {
  app.mount(root)
  console.log('应用已挂载')
} else {
  console.error('挂载失败: #app元素不存在')
  document.body.innerHTML = '<div id="app"></div>'
  app.mount('#app')
}