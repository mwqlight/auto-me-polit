<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="text-2xl font-bold">仪表盘</h1>
      <p class="text-muted">欢迎使用数字身份管理系统</p>
    </div>
    
    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-icon stat-icon--primary">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardData.totalIdentities }}</div>
          <div class="stat-label">总身份数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon stat-icon--success">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardData.linkedAccounts }}</div>
          <div class="stat-label">关联账户</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon stat-icon--warning">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardData.activePolicies }}</div>
          <div class="stat-label">活跃策略</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon stat-icon--info">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ dashboardData.securityScore }}%</div>
          <div class="stat-label">安全评分</div>
        </div>
      </div>
    </div>
    
    <div class="dashboard-content">
      <div class="dashboard-left">
        <!-- 最近活动 -->
        <div class="card">
          <div class="card-header">
            <h3 class="text-lg font-semibold">最近活动</h3>
          </div>
          <div class="card-body">
            <div class="activity-list">
              <div v-for="activity in recentActivities" :key="activity.id" class="activity-item">
                <div class="activity-icon" :class="`activity-icon--${activity.type}`">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                    <path v-if="activity.type === 'identity'" d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                    <path v-else-if="activity.type === 'account'" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                    <path v-else-if="activity.type === 'policy'" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                    <path v-else d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                  </svg>
                </div>
                <div class="activity-content">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-time">{{ formatTime(activity.time) }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-right">
        <!-- 快速操作 -->
        <div class="card">
          <div class="card-header">
            <h3 class="text-lg font-semibold">快速操作</h3>
          </div>
          <div class="card-body">
            <div class="quick-actions">
              <button class="quick-action-btn" @click="$router.push('/identity/create')">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
                创建身份
              </button>
              <button class="quick-action-btn" @click="$router.push('/account/link')">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
                关联账户
              </button>
              <button class="quick-action-btn" @click="$router.push('/policy/create')">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
                </svg>
                创建策略
              </button>
            </div>
          </div>
        </div>
        
        <!-- 安全状态 -->
        <div class="card">
          <div class="card-header">
            <h3 class="text-lg font-semibold">安全状态</h3>
          </div>
          <div class="card-body">
            <div class="security-status">
              <div class="security-item">
                <span class="security-label">身份验证</span>
                <span class="status-indicator status-active"></span>
                <span class="security-status-text">正常</span>
              </div>
              <div class="security-item">
                <span class="security-label">设备绑定</span>
                <span class="status-indicator status-active"></span>
                <span class="security-status-text">已绑定</span>
              </div>
              <div class="security-item">
                <span class="security-label">生物特征</span>
                <span class="status-indicator status-warning"></span>
                <span class="security-status-text">待完善</span>
              </div>
              <div class="security-item">
                <span class="security-label">数据加密</span>
                <span class="status-indicator status-active"></span>
                <span class="security-status-text">已启用</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { identityAPI } from '@/api/modules/identity'
import { accountAPI } from '@/api/modules/account'
import { policyAPI } from '@/api/modules/policy'

// 响应式数据
const dashboardData = ref({
  totalIdentities: 0,
  linkedAccounts: 0,
  activePolicies: 0,
  securityScore: 85
})

const recentActivities = ref([
  {
    id: 1,
    type: 'identity',
    title: '创建了新的数字身份',
    time: new Date(Date.now() - 2 * 60 * 60 * 1000)
  },
  {
    id: 2,
    type: 'account',
    title: '关联了微信账户',
    time: new Date(Date.now() - 4 * 60 * 60 * 1000)
  },
  {
    id: 3,
    type: 'policy',
    title: '更新了数据共享策略',
    time: new Date(Date.now() - 1 * 24 * 60 * 60 * 1000)
  }
])

// 方法
const loadDashboardData = async () => {
  try {
    // 加载身份数据
    const identityResponse = await identityAPI.listIdentities(1, 10)
    dashboardData.value.totalIdentities = identityResponse.data.total || 0
    
    // 加载账户数据
    const accountResponse = await accountAPI.listLinkedAccounts()
    dashboardData.value.linkedAccounts = accountResponse.data.length || 0
    
    // 加载策略数据
    const policyResponse = await policyAPI.listPolicies(1, 10)
    dashboardData.value.activePolicies = policyResponse.data.total || 0
    
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

const formatTime = (time: Date) => {
  const now = new Date()
  const diff = now.getTime() - time.getTime()
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(hours / 24)
  
  if (days > 0) {
    return `${days}天前`
  } else if (hours > 0) {
    return `${hours}小时前`
  } else {
    return '刚刚'
  }
}

// 生命周期
onMounted(() => {
  loadDashboardData()
})
</script>

<style lang="scss" scoped>
.dashboard {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 32px;
  
  h1 {
    margin: 0 0 8px 0;
    color: #1f2937;
  }
  
  p {
    margin: 0;
    color: #6b7280;
  }
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 24px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 2px 0 rgb(0 0 0 / 0.05);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  
  &--primary {
    background-color: #3b82f6;
  }
  
  &--success {
    background-color: #10b981;
  }
  
  &--warning {
    background-color: #f59e0b;
  }
  
  &--info {
    background-color: #06b6d4;
  }
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
}

.dashboard-left,
.dashboard-right {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
  }
}

.activity-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  
  &--identity {
    background-color: #3b82f6;
  }
  
  &--account {
    background-color: #10b981;
  }
  
  &--policy {
    background-color: #f59e0b;
  }
}

.activity-content {
  flex: 1;
}

.activity-title {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 2px;
}

.activity-time {
  font-size: 12px;
  color: #6b7280;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.quick-action-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  color: #1f2937;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: left;
  
  &:hover {
    background-color: #f9fafb;
    border-color: #d1d5db;
  }
  
  svg {
    color: #3b82f6;
  }
}

.security-status {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.security-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.security-label {
  flex: 1;
  font-weight: 500;
  color: #1f2937;
}

.security-status-text {
  font-size: 14px;
  color: #6b7280;
}

@media (max-width: 768px) {
  .dashboard {
    padding: 16px;
  }
  
  .dashboard-stats {
    grid-template-columns: 1fr;
  }
  
  .dashboard-content {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 16px;
  }
}
</style>