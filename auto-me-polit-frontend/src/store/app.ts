import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAppStore = defineStore('app', () => {
  // 状态
  const isLoading = ref(false)
  const theme = ref<'light' | 'dark'>('light')
  const language = ref<'zh-CN' | 'en-US'>('zh-CN')
  const currentPage = ref({
    name: '',
    path: '',
    meta: {}
  })
  
  // 计算属性
  const isDark = computed(() => theme.value === 'dark')
  const currentTitle = computed(() => currentPage.value.meta?.title || '统一身份认证驾驶舱')
  
  // 方法
  const setLoading = (loading: boolean) => {
    isLoading.value = loading
  }
  
  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light'
    // 保存到localStorage
    localStorage.setItem('theme', theme.value)
  }
  
  const setLanguage = (lang: 'zh-CN' | 'en-US') => {
    language.value = lang
    localStorage.setItem('language', lang)
  }
  
  const setCurrentPage = (page: { name: string, path: string, meta: any }) => {
    currentPage.value = page
  }
  
  const initApp = () => {
    // 从localStorage恢复设置
    const savedTheme = localStorage.getItem('theme') as 'light' | 'dark' | null
    const savedLanguage = localStorage.getItem('language') as 'zh-CN' | 'en-US' | null
    
    if (savedTheme) {
      theme.value = savedTheme
    }
    
    if (savedLanguage) {
      language.value = savedLanguage
    }
    
    // 设置body class
    document.body.className = theme.value
  }
  
  return {
    // 状态
    isLoading,
    theme,
    language,
    currentPage,
    
    // 计算属性
    isDark,
    currentTitle,
    
    // 方法
    setLoading,
    toggleTheme,
    setLanguage,
    setCurrentPage,
    initApp
  }
})