import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { http } from '@/api'
import { ElMessage } from 'element-plus'

export interface User {
  id: string
  username: string
  email: string
  displayName?: string
  avatar?: string
  roles: string[]
  permissions: string[]
  isActive: boolean
  createdAt: string
  lastLoginAt?: string
}

export interface AuthTokens {
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const user = ref<User | null>(null)
  const accessToken = ref<string | null>(localStorage.getItem('access_token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refresh_token'))
  const isLoading = ref(false)
  
  // 计算属性
  const isAuthenticated = computed(() => !!accessToken.value && !!user.value)
  const userRoles = computed(() => user.value?.roles || [])
  const userPermissions = computed(() => user.value?.permissions || [])
  const isTokenValid = computed(() => {
    // 简单检查token是否存在
    return !!accessToken.value
  })
  const can = computed(() => {
    return (permission: string) => {
      return userPermissions.value.includes(permission)
    }
  })
  const hasRole = computed(() => {
    return (role: string) => {
      return userRoles.value.includes(role)
    }
  })
  
  // 登录
  const login = async (username: string, password: string) => {
    try {
      isLoading.value = true
      
      const response = await http.post<{
        user: User
        tokens: AuthTokens
      }>('/auth/login', {
        username,
        password
      })
      
      const { user: userData, tokens } = response
      
      // 保存认证信息
      user.value = userData
      accessToken.value = tokens.accessToken
      refreshToken.value = tokens.refreshToken
      
      // 保存到localStorage
      localStorage.setItem('access_token', tokens.accessToken)
      localStorage.setItem('refresh_token', tokens.refreshToken)
      
      ElMessage.success('登录成功')
      return { success: true }
    } catch (error: any) {
      console.error('登录失败:', error)
      ElMessage.error(error.message || '登录失败')
      return { success: false, error: error.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // 注册
  const register = async (data: {
    username: string
    email: string
    password: string
    displayName?: string
  }) => {
    try {
      isLoading.value = true
      
      const response = await http.post<{
        user: User
        tokens: AuthTokens
      }>('/auth/register', data)
      
      const { user: userData, tokens } = response
      
      // 保存认证信息
      user.value = userData
      accessToken.value = tokens.accessToken
      refreshToken.value = tokens.refreshToken
      
      localStorage.setItem('access_token', tokens.accessToken)
      localStorage.setItem('refresh_token', tokens.refreshToken)
      
      ElMessage.success('注册成功')
      return { success: true }
    } catch (error: any) {
      console.error('注册失败:', error)
      ElMessage.error(error.message || '注册失败')
      return { success: false, error: error.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // 刷新token
  const refreshToken = async () => {
    if (!refreshToken.value) {
      return false
    }
    
    try {
      const response = await http.post<AuthTokens>('/auth/refresh', {
        refreshToken: refreshToken.value
      })
      
      accessToken.value = response.accessToken
      localStorage.setItem('access_token', response.accessToken)
      
      return true
    } catch (error) {
      console.error('刷新token失败:', error)
      logout()
      return false
    }
  }
  
  // 登出
  const logout = async () => {
    try {
      // 调用登出API
      if (accessToken.value) {
        await http.post('/auth/logout')
      }
    } catch (error) {
      console.error('登出API调用失败:', error)
    } finally {
      // 清除本地状态
      user.value = null
      accessToken.value = null
      refreshToken.value = null
      
      localStorage.removeItem('access_token')
      localStorage.removeItem('refresh_token')
      
      ElMessage.info('已登出')
    }
  }
  
  // 获取当前用户信息
  const fetchUser = async () => {
    if (!accessToken.value) {
      return
    }
    
    try {
      const userData = await http.get<User>('/auth/me')
      user.value = userData
    } catch (error) {
      console.error('获取用户信息失败:', error)
      logout()
    }
  }
  
  // 更新用户信息
  const updateProfile = async (profileData: Partial<User>) => {
    try {
      isLoading.value = true
      
      const updatedUser = await http.put<User>('/auth/profile', profileData)
      user.value = updatedUser
      
      ElMessage.success('个人信息更新成功')
      return { success: true }
    } catch (error: any) {
      console.error('更新个人信息失败:', error)
      ElMessage.error(error.message || '更新失败')
      return { success: false, error: error.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // 修改密码
  const changePassword = async (data: {
    currentPassword: string
    newPassword: string
  }) => {
    try {
      isLoading.value = true
      
      await http.put('/auth/password', data)
      
      ElMessage.success('密码修改成功')
      return { success: true }
    } catch (error: any) {
      console.error('修改密码失败:', error)
      ElMessage.error(error.message || '密码修改失败')
      return { success: false, error: error.message }
    } finally {
      isLoading.value = false
    }
  }
  
  // 初始化认证状态
  const initAuth = async () => {
    if (accessToken.value) {
      await fetchUser()
    }
  }
  
  return {
    // 状态
    user,
    accessToken,
    isLoading,
    
    // 计算属性
    isAuthenticated,
    userRoles,
    userPermissions,
    isTokenValid,
    can,
    hasRole,
    
    // 方法
    login,
    register,
    refreshToken,
    logout,
    fetchUser,
    updateProfile,
    changePassword,
    initAuth
  }
})