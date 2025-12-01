import { http } from '@/api'

// ==================== 账户关联相关API ====================

export interface LinkedAccount {
  accountId: string
  coreIdentityId: string
  platform: string
  platformAccountId: string
  platformUsername?: string
  accessToken?: string
  refreshToken?: string
  isVerified: boolean
  isActive: boolean
  lastSyncAt: string
  createdAt: string
  syncStatus: 'PENDING' | 'SYNCING' | 'SYNCED' | 'FAILED'
  syncError?: string
}

export interface LinkAccountRequest {
  platform: string
  authorizationCode: string
  redirectUri: string
}

export interface PlatformAccount {
  platform: string
  accountId: string
  username: string
  email?: string
  avatar?: string
  isVerified: boolean
}

export interface AccountVerificationRequest {
  accountId: string
  verificationCode: string
}

export interface SyncAccountRequest {
  accountId: string
  forceSync?: boolean
}

// 账户关联API
export const accountAPI = {
  // 获取关联账户列表
  getList: () => 
    http.get<LinkedAccount[]>('/account/linked'),
  
  // 获取单个关联账户
  get: (accountId: string) => 
    http.get<LinkedAccount>(`/account/linked/${accountId}`),
  
  // 关联第三方账户
  link: (data: LinkAccountRequest) => 
    http.post<LinkedAccount>('/account/link', data),
  
  // 验证关联账户
  verify: (data: AccountVerificationRequest) =>
    http.post('/account/verify', data),
  
  // 解除关联
  unlink: (accountId: string) =>
    http.delete(`/account/linked/${accountId}`),
  
  // 同步账户数据
  sync: (data: SyncAccountRequest) =>
    http.post(`/account/sync`, data),
  
  // 获取支持的平台列表
  getSupportedPlatforms: () =>
    http.get<PlatformAccount[]>('/account/supported-platforms'),
  
  // 获取平台授权URL
  getAuthUrl: (platform: string, redirectUri: string) =>
    http.get<{ authUrl: string }>(`/account/auth-url?platform=${platform}&redirectUri=${redirectUri}`),
  
  // 更新关联账户状态
  updateStatus: (accountId: string, isActive: boolean) =>
    http.put(`/account/linked/${accountId}/status`, { isActive }),
  
  // 批量同步账户
  batchSync: () =>
    http.post('/account/batch-sync'),
  
  // 获取账户统计信息
  getStatistics: () =>
    http.get<{
      totalLinked: number
      verified: number
      active: number
      failed: number
    }>('/account/statistics')
}