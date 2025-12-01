import { createApp } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'

// 导入全局样式
import '@/styles/index.scss'
import 'element-plus/dist/index.css'
import 'nprogress/nprogress.css'

// 导入工具函数
import { setupNProgress } from '@/utils/nprogress'

// 创建应用实例
const app = createApp(App)

// 配置Pinia状态管理
const pinia = createPinia()
app.use(pinia)

// 配置路由
app.use(router)

// 配置NProgress
setupNProgress(router)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
  console.error('Global error:', err, info)
  // 可以在这里集成Sentry等错误监控服务
}

// 挂载应用
app.mount('#app')