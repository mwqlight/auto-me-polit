import { http } from '@/api'

// ==================== 身份管理相关API ====================

export interface CreateIdentityRequest {
  biometricTemplateHash: string
  deviceFingerprint: string
  recoveryContacts: string[]
}

export interface CreateIdentityResponse {
  identityId: string
  publicKey: string
  recoveryKeyFragments: string[]
  isSetupComplete: boolean
  createdAt: string
  securityLevel: number
}

export interface CoreIdentity {
  identityId: string
  publicKey: string
  encryptedPrivateKey: string
  status: 'ACTIVE' | 'FROZEN' | 'DELETED'
  securityLevel: number
  biometricTemplateHash: string
  deviceFingerprint: string
  lastActiveAt: string
  createdAt: string
}

export interface UpdateDeviceFingerprintRequest {
  newDeviceFingerprint: string
}

// 身份管理API
export const identityAPI = {
  // 创建身份
  create: (data: CreateIdentityRequest) => 
    http.post<CreateIdentityResponse>('/identity', data),
  
  // 获取身份信息
  get: (identityId: string) => 
    http.get<CoreIdentity>(`/identity/${identityId}`),
  
  // 更新设备指纹
  updateDeviceFingerprint: (identityId: string, data: UpdateDeviceFingerprintRequest) =>
    http.put(`/identity/${identityId}/device-fingerprint`, data),
  
  // 冻结身份
  freeze: (identityId: string, reason?: string) =>
    http.post(`/identity/${identityId}/freeze`, { reason }),
  
  // 恢复身份
  recover: (identityId: string, reason?: string) =>
    http.post(`/identity/${identityId}/recover`, { reason }),
  
  // 更新最后活跃时间
  updateLastActive: (identityId: string) =>
    http.put(`/identity/${identityId}/last-active`),
  
  // 验证安全级别
  validateSecurityLevel: (identityId: string, level: number) =>
    http.get(`/identity/${identityId}/validate-security-level?level=${level}`)
}