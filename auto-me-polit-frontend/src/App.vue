<template>
  <div id="app" class="min-h-screen bg-gray-50">
    <!-- 加载状态 -->
    <LoadingOverlay v-if="appStore.isLoading" />
    
    <!-- 主要内容区域 -->
    <router-view v-slot="{ Component }">
      <transition name="page" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    
    <!-- 全局通知 -->
    <NotificationContainer />
    
    <!-- 全局模态框 -->
    <ModalContainer />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppStore } from '@/store/app'
import LoadingOverlay from '@/components/common/LoadingOverlay.vue'
import NotificationContainer from '@/components/common/NotificationContainer.vue'
import ModalContainer from '@/components/common/ModalContainer.vue'

// 使用Pinia状态管理
const appStore = useAppStore()

// 应用初始化
onMounted(() => {
  // 检查浏览器兼容性
  checkBrowserCompatibility()
  
  // 初始化应用配置
  appStore.initApp()
  
  // 设置页面标题
  setPageTitle()
})

// 检查浏览器兼容性
function checkBrowserCompatibility() {
  // 检查是否支持现代Web技术
  if (!window.Crypto?.subtle) {
    console.warn('浏览器不支持Web Crypto API，某些功能可能不可用')
  }
  
  if (!window.Promise) {
    alert('浏览器版本过低，请升级到现代浏览器')
    throw new Error('浏览器不支持')
  }
}

// 设置页面标题
function setPageTitle() {
  const route = window.location.pathname
  const titleMap: Record<string, string> = {
    '/dashboard': '统一身份认证驾驶舱 - 仪表盘',
    '/identity': '身份管理',
    '/account': '账户关联',
    '/policy': '共享策略',
    '/settings': '系统设置'
  }
  
  const title = titleMap[route] || '统一身份认证驾驶舱平台'
  document.title = title
}
</script>

<style lang="scss">
#app {
  font-family: 'Inter', 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.page-enter-from {
  opacity: 0;
  transform: translateX(30px);
}

.page-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* 全局滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

/* 元素获取焦点时的样式 */
:focus {
  outline: 2px solid #3b82f6;
  outline-offset: 2px;
}

/* 禁用状态样式 */
.disabled,
:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  pointer-events: none;
}

/* 加载动画 */
.loading-spinner {
  border: 2px solid #f3f4f6;
  border-top: 2px solid #3b82f6;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式断点 */
.container {
  @apply mx-auto px-4 sm:px-6 lg:px-8;
}

/* 工具类 */
.text-truncate {
  @apply truncate;
}

.flex-center {
  @apply flex items-center justify-center;
}

.gradient-bg {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.glass-effect {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.25);
  border: 1px solid rgba(255, 255, 255, 0.18);
}
</style>