<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">策略详情</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p class="loading-text">正在加载策略详情...</p>
        </div>
        
        <div v-else-if="policy" class="policy-details">
          <!-- 策略概览 -->
          <div class="policy-overview">
            <div class="overview-header">
              <div class="policy-icon" :class="`type-${policy.type}`">
                <img v-if="typeIcons[policy.type]" :src="typeIcons[policy.type]" :alt="policy.type" />
                <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                </svg>
              </div>
              <div class="policy-summary">
                <h3 class="policy-name">{{ policy.name }}</h3>
                <p class="policy-description">{{ policy.description || '暂无描述' }}</p>
                <div class="policy-meta">
                  <span class="status-badge" :class="`status-${policy.status}`">
                    {{ getStatusText(policy.status) }}
                  </span>
                  <span class="type-badge" :class="`type-${policy.type}`">
                    {{ getTypeText(policy.type) }}
                  </span>
                  <span class="execution-count">{{ policy.executionCount }} 次执行</span>
                </div>
              </div>
              <div class="overview-actions">
                <button 
                  v-if="policy.status === 'active'"
                  class="action-btn secondary"
                  @click="handleToggleStatus('inactive')"
                  :disabled="actionLoading"
                >
                  禁用
                </button>
                <button 
                  v-else-if="policy.status === 'inactive'"
                  class="action-btn primary"
                  @click="handleToggleStatus('active')"
                  :disabled="actionLoading"
                >
                  启用
                </button>
                <button 
                  v-else-if="policy.status === 'draft'"
                  class="action-btn primary"
                  @click="handleToggleStatus('active')"
                  :disabled="actionLoading"
                >
                  激活
                </button>
                <button 
                  class="action-btn danger"
                  @click="handleDelete"
                  :disabled="actionLoading"
                >
                  删除
                </button>
              </div>
            </div>
          </div>
          
          <!-- 详细信息 -->
          <div class="details-tabs">
            <div class="tab-headers">
              <button
                v-for="tab in tabs"
                :key="tab.key"
                class="tab-header"
                :class="{ active: activeTab === tab.key }"
                @click="activeTab = tab.key"
              >
                <span class="tab-icon">{{ tab.icon }}</span>
                <span class="tab-label">{{ tab.label }}</span>
              </button>
            </div>
            
            <div class="tab-content">
              <!-- 配置信息 -->
              <div v-if="activeTab === 'config'" class="config-section">
                <div class="detail-card">
                  <h4 class="card-title">基本信息</h4>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <label class="detail-label">策略名称</label>
                      <p class="detail-value">{{ policy.name }}</p>
                    </div>
                    <div class="detail-item">
                      <label class="detail-label">策略类型</label>
                      <p class="detail-value">{{ getTypeText(policy.type) }}</p>
                    </div>
                    <div class="detail-item">
                      <label class="detail-label">创建时间</label>
                      <p class="detail-value">{{ formatDateTime(policy.createdAt) }}</p>
                    </div>
                    <div class="detail-item">
                      <label class="detail-label">更新时间</label>
                      <p class="detail-value">{{ formatDateTime(policy.updatedAt) }}</p>
                    </div>
                    <div class="detail-item">
                      <label class="detail-label">执行频率</label>
                      <p class="detail-value">{{ getFrequencyText(policy.config?.executionFrequency) }}</p>
                    </div>
                    <div class="detail-item">
                      <label class="detail-label">数据保留</label>
                      <p class="detail-value">{{ getRetentionText(policy.config?.retentionPolicy) }}</p>
                    </div>
                  </div>
                </div>
                
                <div class="detail-card">
                  <h4 class="card-title">账户配置</h4>
                  <div class="account-flow">
                    <div class="account-node">
                      <div class="account-icon" :class="`platform-${policy.sourceAccount?.platform}`">
                        <img v-if="platformIcons[policy.sourceAccount?.platform]" :src="platformIcons[policy.sourceAccount.platform]" :alt="policy.sourceAccount.platform" />
                        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                        </svg>
                      </div>
                      <div class="account-info">
                        <p class="account-name">{{ policy.sourceAccount?.name }}</p>
                        <p class="account-platform">{{ getPlatformText(policy.sourceAccount?.platform) }}</p>
                      </div>
                    </div>
                    
                    <div class="flow-arrow" :class="{ bidirectional: policy.config?.syncRules?.includes('bidirectional') }">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M10 17l5-5-5-5v10z"/>
                      </svg>
                    </div>
                    
                    <div class="account-node">
                      <div class="account-icon" :class="`platform-${policy.targetAccount?.platform}`">
                        <img v-if="platformIcons[policy.targetAccount?.platform]" :src="platformIcons[policy.targetAccount.platform]" :alt="policy.targetAccount.platform" />
                        <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                        </svg>
                      </div>
                      <div class="account-info">
                        <p class="account-name">{{ policy.targetAccount?.name }}</p>
                        <p class="account-platform">{{ getPlatformText(policy.targetAccount?.platform) }}</p>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="detail-card">
                  <h4 class="card-title">数据配置</h4>
                  <div class="data-config">
                    <div class="config-group">
                      <label class="config-label">数据类型</label>
                      <div class="data-types">
                        <span 
                          v-for="dataType in policy.config?.dataTypes" 
                          :key="dataType"
                          class="data-type-tag"
                        >
                          {{ getDataTypeText(dataType) }}
                        </span>
                      </div>
                    </div>
                    <div class="config-group">
                      <label class="config-label">同步规则</label>
                      <div class="sync-rules">
                        <span 
                          v-for="rule in policy.config?.syncRules" 
                          :key="rule"
                          class="rule-tag"
                          :class="{ active: policy.config?.syncRules?.includes(rule) }"
                        >
                          {{ getSyncRuleText(rule) }}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
                
                <div class="detail-card">
                  <h4 class="card-title">通知设置</h4>
                  <div class="notification-config">
                    <div class="notification-item">
                      <span class="notification-name">成功通知</span>
                      <div class="notification-status" :class="{ active: policy.config?.notifications?.success }">
                        {{ policy.config?.notifications?.success ? '已启用' : '已禁用' }}
                      </div>
                    </div>
                    <div class="notification-item">
                      <span class="notification-name">失败通知</span>
                      <div class="notification-status" :class="{ active: policy.config?.notifications?.failure }">
                        {{ policy.config?.notifications?.failure ? '已启用' : '已禁用' }}
                      </div>
                    </div>
                    <div class="notification-item">
                      <span class="notification-name">冲突通知</span>
                      <div class="notification-status" :class="{ active: policy.config?.notifications?.conflict }">
                        {{ policy.config?.notifications?.conflict ? '已启用' : '已禁用' }}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              
              <!-- 执行历史 -->
              <div v-else-if="activeTab === 'history'" class="history-section">
                <div v-if="policy.executions && policy.executions.length > 0" class="execution-list">
                  <div 
                    v-for="execution in policy.executions" 
                    :key="execution.id"
                    class="execution-item"
                  >
                    <div class="execution-header">
                      <div class="execution-icon" :class="`status-${execution.status}`">
                        <svg v-if="execution.status === 'success'" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                        </svg>
                        <svg v-else-if="execution.status === 'error'" width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                        </svg>
                        <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                          <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                        </svg>
                      </div>
                      <div class="execution-info">
                        <p class="execution-title">
                          {{ getExecutionTitle(execution) }}
                        </p>
                        <p class="execution-time">{{ formatDateTime(execution.startedAt) }}</p>
                      </div>
                      <div class="execution-status" :class="`status-${execution.status}`">
                        {{ getExecutionStatusText(execution.status) }}
                      </div>
                    </div>
                    
                    <div class="execution-details">
                      <div class="detail-row">
                        <span class="detail-label">执行时长</span>
                        <span class="detail-value">{{ getDurationText(execution.duration) }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="detail-label">处理数据</span>
                        <span class="detail-value">{{ getDataCountText(execution.dataCount) }}</span>
                      </div>
                      <div class="detail-row">
                        <span class="detail-label">触发方式</span>
                        <span class="detail-value">{{ getTriggerText(execution.triggerType) }}</span>
                      </div>
                      <div v-if="execution.errorMessage" class="detail-row">
                        <span class="detail-label">错误信息</span>
                        <span class="detail-value error">{{ execution.errorMessage }}</span>
                      </div>
                    </div>
                  </div>
                </div>
                <div v-else class="no-history">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                  </svg>
                  <p class="no-history-text">暂无执行记录</p>
                </div>
              </div>
              
              <!-- 监控统计 -->
              <div v-else-if="activeTab === 'monitoring'" class="monitoring-section">
                <div class="stats-grid">
                  <div class="stat-card">
                    <div class="stat-icon success">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                      </svg>
                    </div>
                    <div class="stat-content">
                      <h3 class="stat-value">{{ policy.stats?.successCount || 0 }}</h3>
                      <p class="stat-label">成功执行</p>
                    </div>
                  </div>
                  
                  <div class="stat-card">
                    <div class="stat-icon error">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                      </svg>
                    </div>
                    <div class="stat-content">
                      <h3 class="stat-value">{{ policy.stats?.errorCount || 0 }}</h3>
                      <p class="stat-label">失败执行</p>
                    </div>
                  </div>
                  
                  <div class="stat-card">
                    <div class="stat-icon average">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                      </svg>
                    </div>
                    <div class="stat-content">
                      <h3 class="stat-value">{{ policy.stats?.averageDuration ? `${policy.stats.averageDuration}ms` : '-' }}</h3>
                      <p class="stat-label">平均耗时</p>
                    </div>
                  </div>
                  
                  <div class="stat-card">
                    <div class="stat-icon data">
                      <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                      </svg>
                    </div>
                    <div class="stat-content">
                      <h3 class="stat-value">{{ policy.stats?.totalDataProcessed || 0 }}</h3>
                      <p class="stat-label">处理数据量</p>
                    </div>
                  </div>
                </div>
                
                <div class="monitoring-chart">
                  <h4 class="chart-title">执行趋势</h4>
                  <div class="chart-placeholder">
                    <svg width="200" height="100" viewBox="0 0 200 100" fill="currentColor">
                      <polyline points="0,80 50,60 100,70 150,40 200,50" stroke="#3b82f6" stroke-width="2" fill="none"/>
                      <circle cx="0" cy="80" r="3" fill="#3b82f6"/>
                      <circle cx="50" cy="60" r="3" fill="#3b82f6"/>
                      <circle cx="100" cy="70" r="3" fill="#3b82f6"/>
                      <circle cx="150" cy="40" r="3" fill="#3b82f6"/>
                      <circle cx="200" cy="50" r="3" fill="#3b82f6"/>
                    </svg>
                    <p class="chart-description">执行成功率趋势图（最近30天）</p>
                  </div>
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
          @click="handleExecuteNow"
        >
          立即执行
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { policyAPI } from '@/api/modules/policy'
import BaseButton from '@/components/base/Button.vue'

// Props
const props = defineProps<{
  policyId?: string
}>()

// 响应式数据
const loading = ref(false)
const actionLoading = ref(false)
const policy = ref<any>(null)
const activeTab = ref('config')

const tabs = [
  {
    key: 'config',
    label: '配置信息',
    icon: '⚙️'
  },
  {
    key: 'history',
    label: '执行历史',
    icon: '📋'
  },
  {
    key: 'monitoring',
    label: '监控统计',
    icon: '📊'
  }
]

const typeIcons: Record<string, string> = {
  sync: '/icons/sync.png',
  share: '/icons/share.png',
  backup: '/icons/backup.png',
  migration: '/icons/migration.png'
}

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
const loadPolicyDetail = async () => {
  if (!props.policyId) return
  
  loading.value = true
  
  try {
    const response = await policyAPI.getPolicyDetail(props.policyId)
    policy.value = response.data
  } catch (error) {
    console.error('加载策略详情失败:', error)
  } finally {
    loading.value = false
  }
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    active: '已启用',
    inactive: '已禁用',
    draft: '草稿'
  }
  return statusMap[status] || status
}

const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    sync: '数据同步',
    share: '数据共享',
    backup: '数据备份',
    migration: '数据迁移'
  }
  return typeMap[type] || type
}

const getPlatformText = (platform: string) => {
  const platformMap: Record<string, string> = {
    wechat: '微信',
    alipay: '支付宝',
    weibo: '微博',
    qq: 'QQ',
    douyin: '抖音',
    github: 'GitHub',
    google: 'Google'
  }
  return platformMap[platform] || platform
}

const getFrequencyText = (frequency: string) => {
  const frequencyMap: Record<string, string> = {
    manual: '手动执行',
    hourly: '每小时',
    daily: '每天',
    weekly: '每周',
    monthly: '每月'
  }
  return frequencyMap[frequency] || frequency
}

const getRetentionText = (retention: string) => {
  const retentionMap: Record<string, string> = {
    '30days': '30天',
    '90days': '90天',
    '1year': '1年',
    'forever': '永久保留'
  }
  return retentionMap[retention] || retention
}

const getDataTypeText = (dataType: string) => {
  const dataTypeMap: Record<string, string> = {
    profile: '个人资料',
    posts: '帖子内容',
    contacts: '联系人',
    media: '媒体文件',
    preferences: '偏好设置',
    activity: '活动记录'
  }
  return dataTypeMap[dataType] || dataType
}

const getSyncRuleText = (rule: string) => {
  const ruleMap: Record<string, string> = {
    realTime: '实时同步',
    bidirectional: '双向同步',
    conflictResolve: '冲突自动解决'
  }
  return ruleMap[rule] || rule
}

const getExecutionTitle = (execution: any) => {
  const titleMap: Record<string, string> = {
    'manual': '手动执行',
    'scheduled': '定时执行',
    'triggered': '触发执行'
  }
  return titleMap[execution.type] || execution.type
}

const getExecutionStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    success: '成功',
    error: '失败',
    running: '执行中'
  }
  return statusMap[status] || status
}

const getDurationText = (duration: number) => {
  if (!duration) return '-'
  if (duration < 1000) return `${duration}ms`
  return `${(duration / 1000).toFixed(1)}s`
}

const getDataCountText = (count: number) => {
  if (!count) return '-'
  return `${count} 条`
}

const getTriggerText = (trigger: string) => {
  const triggerMap: Record<string, string> = {
    manual: '手动触发',
    scheduled: '定时任务',
    webhook: 'Webhook',
    api: 'API调用'
  }
  return triggerMap[trigger] || trigger
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

const handleToggleStatus = async (newStatus: string) => {
  if (!props.policyId) return
  
  actionLoading.value = true
  
  try {
    await policyAPI.updatePolicyStatus(props.policyId, newStatus)
    await loadPolicyDetail()
    $emit('updated')
  } catch (error) {
    console.error('更新策略状态失败:', error)
    alert('更新策略状态失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const handleDelete = async () => {
  if (!props.policyId) return
  
  if (!confirm('确定要删除此策略吗？此操作不可恢复。')) {
    return
  }
  
  actionLoading.value = true
  
  try {
    await policyAPI.deletePolicy(props.policyId)
    $emit('deleted')
  } catch (error) {
    console.error('删除策略失败:', error)
    alert('删除策略失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const handleExecuteNow = async () => {
  if (!props.policyId) return
  
  actionLoading.value = true
  
  try {
    await policyAPI.executePolicy(props.policyId)
    await loadPolicyDetail()
    alert('执行请求已发送')
    $emit('updated')
  } catch (error) {
    console.error('执行策略失败:', error)
    alert('执行策略失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const $emit = defineEmits<{
  close: []
  updated: []
  deleted: []
}>()

// 生命周期
onMounted(() => {
  loadPolicyDetail()
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
  max-width: 1000px;
  width: 95vw;
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
  padding: 0;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  
  .loading-text {
    margin-top: 16px;
    color: #6b7280;
  }
}

.policy-overview {
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.overview-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
}

.policy-icon {
  width: 64px;
  height: 64px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  flex-shrink: 0;
  
  &.type-sync {
    background-color: rgba(59, 130, 246, 0.2);
  }
  
  &.type-share {
    background-color: rgba(217, 119, 6, 0.2);
  }
  
  &.type-backup {
    background-color: rgba(16, 185, 129, 0.2);
  }
  
  &.type-migration {
    background-color: rgba(239, 68, 68, 0.2);
  }
  
  img {
    width: 32px;
    height: 32px;
  }
  
  svg {
    width: 32px;
    height: 32px;
  }
}

.policy-summary {
  flex: 1;
  
  .policy-name {
    font-size: 24px;
    font-weight: 700;
    margin: 0 0 8px 0;
  }
  
  .policy-description {
    font-size: 14px;
    margin: 0 0 12px 0;
    opacity: 0.9;
  }
  
  .policy-meta {
    display: flex;
    align-items: center;
    gap: 8px;
    flex-wrap: wrap;
  }
}

.status-badge,
.type-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  
  &.status-active {
    background-color: rgba(34, 197, 94, 0.2);
    color: #10b981;
  }
  
  &.status-inactive {
    background-color: rgba(107, 114, 128, 0.2);
    color: #9ca3af;
  }
  
  &.status-draft {
    background-color: rgba(245, 158, 11, 0.2);
    color: #f59e0b;
  }
  
  &.type-sync {
    background-color: rgba(59, 130, 246, 0.2);
    color: #3b82f6;
  }
  
  &.type-share {
    background-color: rgba(217, 119, 6, 0.2);
    color: #d97706;
  }
  
  &.type-backup {
    background-color: rgba(16, 185, 129, 0.2);
    color: #10b981;
  }
  
  &.type-migration {
    background-color: rgba(239, 68, 68, 0.2);
    color: #ef4444;
  }
}

.execution-count {
  font-size: 12px;
  opacity: 0.8;
}

.overview-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover:not(:disabled) {
    background-color: rgba(255, 255, 255, 0.2);
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
  
  &.primary {
    background-color: rgba(255, 255, 255, 0.2);
    border-color: rgba(255, 255, 255, 0.4);
  }
  
  &.secondary {
    background-color: rgba(255, 255, 255, 0.1);
    border-color: rgba(255, 255, 255, 0.3);
  }
  
  &.danger {
    background-color: rgba(239, 68, 68, 0.2);
    border-color: rgba(239, 68, 68, 0.4);
  }
}

.details-tabs {
  .tab-headers {
    display: flex;
    background-color: #f9fafb;
    border-bottom: 1px solid #e5e7eb;
  }
  
  .tab-header {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px 24px;
    border: none;
    background: none;
    font-size: 14px;
    color: #6b7280;
    cursor: pointer;
    border-bottom: 2px solid transparent;
    transition: all 0.2s ease;
    
    &:hover {
      color: #374151;
      background-color: rgba(0, 0, 0, 0.02);
    }
    
    &.active {
      color: #3b82f6;
      border-bottom-color: #3b82f6;
      background-color: white;
    }
  }
}

.tab-content {
  padding: 24px;
}

.detail-card {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
  
  .card-title {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 16px 0;
  }
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
  
  &.error {
    color: #ef4444;
  }
}

.account-flow {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 24px;
  background-color: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.account-node {
  display: flex;
  align-items: center;
  gap: 12px;
  
  .account-icon {
    width: 48px;
    height: 48px;
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f3f4f6;
    color: #6b7280;
    flex-shrink: 0;
    
    &.platform-wechat {
      background-color: #07c160;
      color: white;
    }
    
    &.platform-alipay {
      background-color: #00a0e9;
      color: white;
    }
    
    &.platform-weibo {
      background-color: #e6162d;
      color: white;
    }
    
    img {
      width: 24px;
      height: 24px;
    }
    
    svg {
      width: 24px;
      height: 24px;
    }
  }
  
  .account-info {
    .account-name {
      font-size: 14px;
      font-weight: 500;
      color: #1f2937;
      margin: 0 0 2px 0;
    }
    
    .account-platform {
      font-size: 12px;
      color: #6b7280;
      margin: 0;
    }
  }
}

.flow-arrow {
  color: #9ca3af;
  flex-shrink: 0;
  
  &.bidirectional {
    transform: rotate(180deg);
  }
}

.data-config {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.config-group {
  .config-label {
    display: block;
    font-size: 12px;
    font-weight: 500;
    color: #6b7280;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 8px;
  }
}

.data-types,
.sync-rules {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.data-type-tag,
.rule-tag {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background-color: #e5e7eb;
  color: #374151;
  
  &.active {
    background-color: #dbeafe;
    color: #1d4ed8;
  }
}

.notification-config {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  background-color: white;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.notification-name {
  font-size: 14px;
  font-weight: 500;
  color: #1f2937;
}

.notification-status {
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

.execution-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.execution-item {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e5e7eb;
}

.execution-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.execution-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  
  &.status-success {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.status-error {
    background-color: #fecaca;
    color: #dc2626;
  }
  
  &.status-running {
    background-color: #dbeafe;
    color: #1d4ed8;
  }
}

.execution-info {
  flex: 1;
  
  .execution-title {
    font-size: 14px;
    font-weight: 500;
    color: #1f2937;
    margin: 0 0 4px 0;
  }
  
  .execution-time {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
  }
}

.execution-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  
  &.status-success {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.status-error {
    background-color: #fecaca;
    color: #dc2626;
  }
  
  &.status-running {
    background-color: #dbeafe;
    color: #1d4ed8;
  }
}

.execution-details {
  .detail-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4px 0;
    
    .detail-label {
      font-size: 12px;
      color: #6b7280;
    }
    
    .detail-value {
      font-size: 12px;
      color: #1f2937;
      
      &.error {
        color: #ef4444;
      }
    }
  }
}

.no-history {
  text-align: center;
  padding: 60px 20px;
  color: #9ca3af;
  
  svg {
    margin-bottom: 16px;
  }
  
  .no-history-text {
    font-size: 14px;
    margin: 0;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  border: 1px solid #e5e7eb;
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  
  &.success {
    background-color: #10b981;
  }
  
  &.error {
    background-color: #ef4444;
  }
  
  &.average {
    background-color: #3b82f6;
  }
  
  &.data {
    background-color: #8b5cf6;
  }
}

.stat-content {
  .stat-value {
    font-size: 20px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 4px 0;
  }
  
  .stat-label {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
  }
}

.monitoring-chart {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 20px;
  
  .chart-title {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 16px 0;
  }
}

.chart-placeholder {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  
  .chart-description {
    font-size: 14px;
    margin: 16px 0 0 0;
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
  
  .overview-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .overview-actions {
    align-self: stretch;
    justify-content: stretch;
    
    .action-btn {
      flex: 1;
    }
  }
  
  .policy-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .tab-headers {
    overflow-x: auto;
  }
  
  .tab-header {
    flex-shrink: 0;
  }
  
  .account-flow {
    flex-direction: column;
  }
  
  .flow-arrow {
    transform: rotate(90deg);
    
    &.bidirectional {
      transform: rotate(270deg);
    }
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-footer {
    flex-direction: column-reverse;
  }
}
</style>