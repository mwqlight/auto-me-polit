import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/auth'

// 创建axios实例
const apiClient: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const authStore = useAuthStore()
    
    // 添加认证token
    if (authStore.accessToken) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${authStore.accessToken}`
      }
    }
    
    // 添加请求ID用于追踪
    config.headers = {
      ...config.headers,
      'X-Request-ID': crypto.randomUUID()
    }
    
    // 添加时间戳防止缓存
    if (config.method === 'get') {
      config.params = {
        ...config.params,
        _t: Date.now()
      }
    }
    
    console.log(`🚀 API请求: ${config.method?.toUpperCase()} ${config.url}`, config.data || config.params)
    
    return config
  },
  (error) => {
    console.error('❌ 请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    const config = response.config
    
    console.log(`✅ API响应: ${config.method?.toUpperCase()} ${config.url}`, data)
    
    // 处理业务响应格式
    if (data.code !== undefined) {
      if (data.code === 200) {
        return data.data ?? data
      } else {
        // 业务错误
        const errorMsg = data.message || '请求失败'
        ElMessage.error(errorMsg)
        return Promise.reject(new Error(errorMsg))
      }
    }
    
    return data
  },
  (error) => {
    console.error('❌ 响应错误:', error)
    
    const { response } = error
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          // Token过期或无效，清除认证信息并跳转到登录页
          const authStore = useAuthStore()
          authStore.logout()
          window.location.href = '/auth/login'
          ElMessage.error('认证已过期，请重新登录')
          break
          
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
          
        case 404:
          ElMessage.error('请求的资源不存在')
          break
          
        case 429:
          ElMessage.error('请求过于频繁，请稍后重试')
          break
          
        case 500:
          ElMessage.error('服务器内部错误')
          break
          
        default:
          const message = data?.message || `请求失败 (${status})`
          ElMessage.error(message)
      }
      
      return Promise.reject(error)
    } else if (error.request) {
      // 网络错误
      ElMessage.error('网络连接失败，请检查网络设置')
    } else {
      // 其他错误
      ElMessage.error('请求配置错误')
    }
    
    return Promise.reject(error)
  }
)

export { apiClient }

// 封装常用的HTTP方法
export const http = {
  get: <T = any>(url: string, params?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.get(url, { params, ...config }).then(res => res.data),
    
  post: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.post(url, data, config).then(res => res.data),
    
  put: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.put(url, data, config).then(res => res.data),
    
  patch: <T = any>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.patch(url, data, config).then(res => res.data),
    
  delete: <T = any>(url: string, config?: AxiosRequestConfig): Promise<T> =>
    apiClient.delete(url, config).then(res => res.data)
}

// 文件上传
export const uploadFile = (url: string, file: File, onProgress?: (progress: number) => void): Promise<any> => {
  const formData = new FormData()
  formData.append('file', file)
  
  return apiClient.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress: (progressEvent) => {
      if (onProgress && progressEvent.total) {
        const progress = Math.round((progressEvent.loaded * 100) / progressEvent.total)
        onProgress(progress)
      }
    }
  }).then(res => res.data)
}

// 下载文件
export const downloadFile = (url: string, filename?: string): Promise<void> => {
  return apiClient.get(url, {
    responseType: 'blob'
  }).then(response => {
    const blob = new Blob([response.data])
    const downloadUrl = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = downloadUrl
    link.download = filename || 'download'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(downloadUrl)
  })
}