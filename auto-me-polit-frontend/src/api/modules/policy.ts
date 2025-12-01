import { http } from '@/api'

// ==================== 共享策略相关API ====================

export interface SharePolicy {
  policyId: string
  coreIdentityId: string
  requesterIdentifier: string
  shareConfig: ShareConfig
  duration: number
  constraints: ShareConstraints
  status: 'ACTIVE' | 'EXPIRED' | 'REVOKED'
  createdAt: string
  expiresAt: string
}

export interface ShareConfig {
  sharedAttributes: string[]
  accessLevel: 'READ' | 'WRITE' | 'ADMIN'
  allowedOperations: string[]
  dataRetentionPeriod: number
}

export interface ShareConstraints {
  ipRestrictions?: string[]
  timeRestrictions?: TimeRestriction[]
  deviceRestrictions?: DeviceRestriction[]
  maximumRequests?: number
}

export interface TimeRestriction {
  type: 'DAILY' | 'WEEKLY' | 'MONTHLY'
  allowedTimes: string[]
}

export interface DeviceRestriction {
  deviceTypes: ('MOBILE' | 'DESKTOP' | 'TABLET')[]
  allowedDevices?: string[]
}

export interface CreateSharePolicyRequest {
  requesterIdentifier: string
  sharedAttributes: string[]
  accessLevel: 'READ' | 'WRITE' | 'ADMIN'
  duration: number
  constraints?: ShareConstraints
}

export interface UpdateSharePolicyRequest {
  sharedAttributes?: string[]
  accessLevel?: 'READ' | 'WRITE' | 'ADMIN'
  duration?: number
  constraints?: ShareConstraints
}

export interface SharePolicyValidation {
  isValid: boolean
  errors: string[]
  warnings: string[]
  estimatedRiskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'
}

// 共享策略API
export const policyAPI = {
  // 获取策略列表
  getList: () => 
    http.get<SharePolicy[]>('/policy'),
  
  // 获取单个策略
  get: (policyId: string) => 
    http.get<SharePolicy>(`/policy/${policyId}`),
  
  // 创建共享策略
  create: (data: CreateSharePolicyRequest) => 
    http.post<SharePolicy>('/policy', data),
  
  // 更新共享策略
  update: (policyId: string, data: UpdateSharePolicyRequest) =>
    http.put<SharePolicy>(`/policy/${policyId}`, data),
  
  // 删除共享策略
  delete: (policyId: string) =>
    http.delete(`/policy/${policyId}`),
  
  // 撤销共享策略
  revoke: (policyId: string, reason?: string) =>
    http.post(`/policy/${policyId}/revoke`, { reason }),
  
  // 验证策略配置
  validate: (data: CreateSharePolicyRequest) =>
    http.post<SharePolicyValidation>('/policy/validate', data),
  
  // 获取策略使用统计
  getUsage: (policyId: string) =>
    http.get<{
      totalRequests: number
      successfulRequests: number
      failedRequests: number
      averageResponseTime: number
      lastRequestAt: string
    }>(`/policy/${policyId}/usage`),
  
  // 获取共享请求记录
  getAccessLog: (policyId: string, page?: number, size?: number) =>
    http.get<{
      items: Array<{
        timestamp: string
        requester: string
        operation: string
        result: 'SUCCESS' | 'FAILURE'
        ipAddress: string
      }>
      total: number
    }>(`/policy/${policyId}/access-log?page=${page || 1}&size=${size || 20}`)
}