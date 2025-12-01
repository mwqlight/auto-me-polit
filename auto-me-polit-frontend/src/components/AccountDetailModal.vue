<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">账户详情</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p class="loading-text">正在加载账户信息...</p>
        </div>
        
        <div v-else-if="account" class="account-details">
          <div class="account-header">
            <div class="account-icon" :class="`platform-${account.platform}`">
              <img v-if="platformIcons[account.platform]" :src="platformIcons[account.platform]" :alt="account.platform" />
              <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <div class="account-info">
              <h3 class="account-name">{{ account.platformName }}</h3>
              <p class="account-status" :class="`status-${account.status}`">
                {{ getStatusText(account.status) }}
              </p>
            </div>
            <div class="account-actions">
              <button 
                v-if="account.status === 'active'"
                class="action-btn danger"
                @click="handleUnlink"
              >
                解除关联
              </button>
              <button 
                v-else-if="account.status === 'inactive'"
                class="action-btn primary"
                @click="handleReactivate"
              >
                重新激活
              </button>
            </div>
          </div>
          
          <div class="detail-sections">
            <div class="detail-section">
              <h4 class="section-title">基本信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label class="detail-label">平台ID</label>
                  <p class="detail-value">{{ account.platformId || '-' }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">关联时间</label>
                  <p class="detail-value">{{ formatDateTime(account.connectedAt) }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">最后同步</label>
                  <p class="detail-value">{{ formatDateTime(account.lastSyncAt) || '从未同步' }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">同步状态</label>
                  <p class="detail-value" :class="`sync-${account.syncStatus}`">
                    {{ getSyncStatusText(account.syncStatus) }}
                  </p>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4 class="section-title">账户信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <label class="detail-label">用户名</label>
                  <p class="detail-value">{{ account.username || '-' }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">邮箱</label>
                  <p class="detail-value">{{ account.email || '-' }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">昵称</label>
                  <p class="detail-value">{{ account.nickname || '-' }}</p>
                </div>
                <div class="detail-item">
                  <label class="detail-label">头像</label>
                  <div class="avatar-container">
                    <img 
                      v-if="account.avatarUrl" 
                      :src="account.avatarUrl" 
                      :alt="account.nickname || account.username"
                      class="avatar"
                    />
                    <div v-else class="avatar-placeholder">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4 class="section-title">配置信息</h4>
              <div class="config-list">
                <div class="config-item">
                  <div class="config-info">
                    <span class="config-name">自动同步</span>
                    <span class="config-description">自动同步账户数据</span>
                  </div>
                  <div class="config-status" :class="{ active: account.config?.autoSync }">
                    {{ account.config?.autoSync ? '已启用' : '已禁用' }}
                  </div>
                </div>
                <div class="config-item">
                  <div class="config-info">
                    <span class="config-name">通知</span>
                    <span class="config-description">接收平台相关通知</span>
                  </div>
                  <div class="config-status" :class="{ active: account.config?.enableNotifications }">
                    {{ account.config?.enableNotifications ? '已启用' : '已禁用' }}
                  </div>
                </div>
                <div class="config-item">
                  <div class="config-info">
                    <span class="config-name">共享资料</span>
                    <span class="config-description">与平台共享个人资料</span>
                  </div>
                  <div class="config-status" :class="{ active: account.config?.shareProfile }">
                    {{ account.config?.shareProfile ? '已启用' : '已禁用' }}
                  </div>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4 class="section-title">同步历史</h4>
              <div class="sync-history">
                <div 
                  v-if="account.syncHistory && account.syncHistory.length > 0"
                  class="history-list"
                >
                  <div 
                    v-for="(sync, index) in account.syncHistory" 
                    :key="index"
                    class="history-item"
                  >
                    <div class="history-icon" :class="`sync-${sync.status}`">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path v-if="sync.status === 'success'" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                        <path v-else-if="sync.status === 'error'" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                        <path v-else d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                      </svg>
                    </div>
                    <div class="history-content">
                      <p class="history-title">{{ getSyncHistoryTitle(sync) }}</p>
                      <p class="history-time">{{ formatDateTime(sync.timestamp) }}</p>
                      <p v-if="sync.message" class="history-message">{{ sync.message }}</p>
                    </div>
                  </div>
                </div>
                <div v-else class="no-history">
                  <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  <p class="no-history-text">暂无同步记录</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <BaseButton variant="secondary" @click="$emit('close')">
          关闭
        </BaseButton>
        <BaseButton 
          variant="primary" 
          :loading="actionLoading"
          @click="handleSync"
        >
          立即同步
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { accountAPI } from '@/api/modules/account'
import BaseButton from '@/components/base/Button.vue'

// Props
const props = defineProps<{
  accountId?: string
}>()

// 响应式数据
const loading = ref(false)
const actionLoading = ref(false)
const account = ref<any>(null)

const platformIcons: Record<string, string> = {
  wechat: '/icons/wechat.png',
  alipay: '/icons/alipay.png',
  weibo: '/icons/weibo.png',
  qq: '/icons/qq.png',
  douyin: '/icons/douyin.png',
  github: '/icons/github.png',
  google: '/icons/google.png'
}

// 方法
const loadAccountDetail = async () => {
  if (!props.accountId) return
  
  loading.value = true
  
  try {
    const response = await accountAPI.getAccountDetail(props.accountId)
    account.value = response.data
  } catch (error) {
    console.error('加载账户详情失败:', error)
  } finally {
    loading.value = false
  }
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    active: '已激活',
    inactive: '已停用',
    suspended: '已暂停',
    error: '连接异常'
  }
  return statusMap[status] || status
}

const getSyncStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    idle: '空闲',
    syncing: '同步中',
    success: '同步成功',
    error: '同步失败'
  }
  return statusMap[status] || status
}

const getSyncHistoryTitle = (sync: any) => {
  const titleMap: Record<string, string> = {
    'profile_update': '个人资料更新',
    'data_sync': '数据同步',
    'avatar_update': '头像更新',
    'account_verify': '账户验证',
    'connection_refresh': '连接刷新'
  }
  return titleMap[sync.type] || sync.type
}

const formatDateTime = (dateString: string) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).format(date)
}

const handleSync = async () => {
  if (!props.accountId) return
  
  actionLoading.value = true
  
  try {
    await accountAPI.syncAccountData(props.accountId)
    
    // 重新加载账户信息
    await loadAccountDetail()
    
    alert('同步请求已发送，请稍后查看结果')
  } catch (error) {
    console.error('同步失败:', error)
    alert('同步失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const handleUnlink = async () => {
  if (!props.accountId) return
  
  if (!confirm('确定要解除关联此账户吗？解除后将无法继续使用该平台的同步服务。')) {
    return
  }
  
  actionLoading.value = true
  
  try {
    await accountAPI.unlinkAccount(props.accountId)
    
    // 重新加载账户信息
    await loadAccountDetail()
    
    $emit('close')
    $emit('unlinked')
  } catch (error) {
    console.error('解除关联失败:', error)
    alert('解除关联失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const handleReactivate = async () => {
  if (!props.accountId) return
  
  actionLoading.value = true
  
  try {
    await accountAPI.reactivateAccount(props.accountId)
    
    // 重新加载账户信息
    await loadAccountDetail()
    
    $emit('close')
    $emit('reactivated')
  } catch (error) {
    console.error('重新激活失败:', error)
    alert('重新激活失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const $emit = defineEmits<{
  close: []
  unlinked: []
  reactivated: []
  synced: []
}>()

// 生命周期
onMounted(() => {
  loadAccountDetail()
})
</script>

<style lang="scss" scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  box-shadow: 0 20px 25px -5px rgb(0 0 0 / 0.1), 0 8px 10px -6px rgb(0 0 0 / 0.1);
  max-width: 800px;
  width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
  
  h2 {
    margin: 0;
    color: #1f2937;
  }
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #6b7280;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f3f4f6;
    color: #374151;
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  
  .loading-text {
    margin-top: 16px;
    color: #6b7280;
  }
}

.account-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.account-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  
  &.platform-wechat {
    background-color: rgba(7, 193, 96, 0.2);
  }
  
  &.platform-alipay {
    background-color: rgba(0, 160, 233, 0.2);
  }
  
  &.platform-weibo {
    background-color: rgba(230, 22, 45, 0.2);
  }
  
  &.platform-qq {
    background-color: rgba(18, 183, 245, 0.2);
  }
  
  &.platform-douyin {
    background-color: rgba(0, 0, 0, 0.2);
  }
  
  &.platform-github {
    background-color: rgba(51, 51, 51, 0.2);
  }
  
  &.platform-google {
    background-color: rgba(66, 133, 244, 0.2);
  }
  
  img {
    width: 40px;
    height: 40px;
  }
  
  svg {
    width: 40px;
    height: 40px;
  }
}

.account-info {
  flex: 1;
}

.account-name {
  font-size: 24px;
  font-weight: 700;
  margin: 0 0 4px 0;
}

.account-status {
  margin: 0;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
  background-color: rgba(255, 255, 255, 0.2);
  
  &.status-active {
    background-color: rgba(34, 197, 94, 0.2);
  }
  
  &.status-inactive {
    background-color: rgba(107, 114, 128, 0.2);
  }
  
  &.status-suspended {
    background-color: rgba(239, 68, 68, 0.2);
  }
  
  &.status-error {
    background-color: rgba(245, 101, 101, 0.2);
  }
}

.account-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &.primary {
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
    border: 1px solid rgba(255, 255, 255, 0.3);
    
    &:hover {
      background-color: rgba(255, 255, 255, 0.3);
    }
  }
  
  &.danger {
    background-color: rgba(239, 68, 68, 0.2);
    color: white;
    border: 1px solid rgba(239, 68, 68, 0.3);
    
    &:hover {
      background-color: rgba(239, 68, 68, 0.3);
    }
  }
}

.detail-sections {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
  
  &.sync-success {
    color: #10b981;
  }
  
  &.sync-error {
    color: #ef4444;
  }
  
  &.sync-syncing {
    color: #3b82f6;
  }
}

.avatar-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
}

.config-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.config-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.config-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.config-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.config-description {
  font-size: 12px;
  color: #6b7280;
}

.config-status {
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 12px;
  background-color: #f3f4f6;
  color: #6b7280;
  
  &.active {
    background-color: #dcfce7;
    color: #166534;
  }
}

.sync-history {
  max-height: 240px;
  overflow-y: auto;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.history-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  &.sync-success {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.sync-error {
    background-color: #fecaca;
    color: #dc2626;
  }
  
  &.sync-idle {
    background-color: #f3f4f6;
    color: #6b7280;
  }
}

.history-content {
  flex: 1;
}

.history-title {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.history-time {
  font-size: 12px;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.history-message {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
  line-height: 1.4;
}

.no-history {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  
  svg {
    margin-bottom: 16px;
  }
  
  .no-history-text {
    font-size: 14px;
    margin: 0;
  }
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95vw;
    margin: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .account-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .account-actions {
    align-self: stretch;
    justify-content: stretch;
    
    .action-btn {
      flex: 1;
    }
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-footer {
    flex-direction: column-reverse;
  }
}
</style>