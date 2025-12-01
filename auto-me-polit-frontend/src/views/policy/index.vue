<template>
  <div class="policy-management">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">共享策略管理</h1>
        <p class="page-description">管理和配置账户间数据共享策略</p>
      </div>
      <div class="header-actions">
        <BaseButton variant="primary" @click="showCreateModal = true">
          <template #icon>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
          </template>
          创建策略
        </BaseButton>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="search-section">
      <div class="search-bar">
        <div class="search-input-wrapper">
          <svg class="search-icon" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
            <path d="M15.5 14h-.79l-.28-.27C15.41 12.59 16 11.11 16 9.5 16 5.91 13.09 3 9.5 3S3 5.91 3 9.5 5.91 16 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索策略名称或描述..."
            class="search-input"
          />
        </div>
      </div>
      
      <div class="filter-bar">
        <div class="filter-group">
          <label class="filter-label">状态</label>
          <select v-model="statusFilter" class="filter-select">
            <option value="">全部状态</option>
            <option value="active">已启用</option>
            <option value="inactive">已禁用</option>
            <option value="draft">草稿</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label class="filter-label">策略类型</label>
          <select v-model="typeFilter" class="filter-select">
            <option value="">全部类型</option>
            <option value="sync">数据同步</option>
            <option value="share">数据共享</option>
            <option value="backup">数据备份</option>
            <option value="migration">数据迁移</option>
          </select>
        </div>
        
        <button class="reset-filter-btn" @click="resetFilters">
          重置筛选
        </button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background-color: #dbeafe;">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.total }}</h3>
          <p class="stat-label">总策略数</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background-color: #dcfce7;">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.active }}</h3>
          <p class="stat-label">已启用</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background-color: #fef3c7;">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.draft }}</h3>
          <p class="stat-label">草稿</p>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon" style="background-color: #fecaca;">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
        </div>
        <div class="stat-content">
          <h3 class="stat-value">{{ stats.totalExecutions }}</h3>
          <p class="stat-label">总执行次数</p>
        </div>
      </div>
    </div>

    <!-- 策略列表 -->
    <div class="policy-list">
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p class="loading-text">正在加载策略列表...</p>
      </div>
      
      <div v-else-if="filteredPolicies.length === 0" class="empty-state">
        <svg class="empty-icon" width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
          <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
        </svg>
        <h3 class="empty-title">暂无共享策略</h3>
        <p class="empty-description">
          {{ searchQuery || statusFilter || typeFilter ? '没有找到符合条件的策略' : '创建第一个共享策略开始管理您的账户数据' }}
        </p>
        <BaseButton v-if="!searchQuery && !statusFilter && !typeFilter" variant="primary" @click="showCreateModal = true">
          创建策略
        </BaseButton>
      </div>
      
      <div v-else class="policies-grid">
        <div
          v-for="policy in paginatedPolicies"
          :key="policy.id"
          class="policy-card"
        >
          <div class="policy-header">
            <div class="policy-icon" :class="`type-${policy.type}`">
              <img v-if="typeIcons[policy.type]" :src="typeIcons[policy.type]" :alt="policy.type" />
              <svg v-else width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
              </svg>
            </div>
            <div class="policy-info">
              <h3 class="policy-name">{{ policy.name }}</h3>
              <p class="policy-description">{{ policy.description }}</p>
            </div>
            <div class="policy-status" :class="`status-${policy.status}`">
              {{ getStatusText(policy.status) }}
            </div>
          </div>
          
          <div class="policy-details">
            <div class="detail-row">
              <span class="detail-label">策略类型</span>
              <span class="detail-value">{{ getTypeText(policy.type) }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">源账户</span>
              <span class="detail-value">{{ policy.sourceAccount?.name || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">目标账户</span>
              <span class="detail-value">{{ policy.targetAccount?.name || '-' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">执行次数</span>
              <span class="detail-value">{{ policy.executionCount }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">最后执行</span>
              <span class="detail-value">{{ formatDateTime(policy.lastExecutedAt) || '从未执行' }}</span>
            </div>
            <div class="detail-row">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatDateTime(policy.createdAt) }}</span>
            </div>
          </div>
          
          <div class="policy-actions">
            <button class="action-btn" @click="viewPolicyDetail(policy)">
              查看详情
            </button>
            <button 
              v-if="policy.status === 'draft'"
              class="action-btn primary"
              @click="activatePolicy(policy)"
            >
              启用策略
            </button>
            <button 
              v-else-if="policy.status === 'active'"
              class="action-btn secondary"
              @click="deactivatePolicy(policy)"
            >
              禁用策略
            </button>
            <button class="action-btn danger" @click="deletePolicy(policy)">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="filteredPolicies.length > itemsPerPage" class="pagination">
      <button
        class="page-btn"
        :disabled="currentPage === 1"
        @click="currentPage = currentPage - 1"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
          <path d="M15.41 7.41L14 6l-6 6 6 6 1.41-1.41L10.83 12z"/>
        </svg>
        上一页
      </button>
      
      <div class="page-numbers">
        <button
          v-for="page in visiblePages"
          :key="page"
          class="page-number"
          :class="{ active: page === currentPage }"
          @click="currentPage = page"
        >
          {{ page }}
        </button>
      </div>
      
      <button
        class="page-btn"
        :disabled="currentPage === totalPages"
        @click="currentPage = currentPage + 1"
      >
        下一页
        <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
          <path d="M10 6L8.59 7.41 13.17 12l-4.58 4.59L10 18l6-6z"/>
        </svg>
      </button>
    </div>

    <!-- 创建策略模态框 -->
    <CreatePolicyModal 
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @created="handlePolicyCreated"
    />
    
    <!-- 策略详情模态框 -->
    <PolicyDetailModal 
      v-if="showDetailModal"
      :policy-id="selectedPolicyId"
      @close="showDetailModal = false"
      @updated="handlePolicyUpdated"
      @deleted="handlePolicyDeleted"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { policyAPI } from '@/api/modules/policy'
import BaseButton from '@/components/base/Button.vue'
import CreatePolicyModal from '@/components/CreatePolicyModal.vue'
import PolicyDetailModal from '@/components/PolicyDetailModal.vue'

// 响应式数据
const loading = ref(false)
const showCreateModal = ref(false)
const showDetailModal = ref(false)
const selectedPolicyId = ref<string>('')

const searchQuery = ref('')
const statusFilter = ref('')
const typeFilter = ref('')
const currentPage = ref(1)
const itemsPerPage = 12

const policies = ref<any[]>([])
const stats = ref({
  total: 0,
  active: 0,
  draft: 0,
  totalExecutions: 0
})

const typeIcons: Record<string, string> = {
  sync: '/icons/sync.png',
  share: '/icons/share.png',
  backup: '/icons/backup.png',
  migration: '/icons/migration.png'
}

// 计算属性
const filteredPolicies = computed(() => {
  let filtered = policies.value
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(policy =>
      policy.name.toLowerCase().includes(query) ||
      policy.description.toLowerCase().includes(query)
    )
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter(policy => policy.status === statusFilter.value)
  }
  
  if (typeFilter.value) {
    filtered = filtered.filter(policy => policy.type === typeFilter.value)
  }
  
  return filtered
})

const totalPages = computed(() => Math.ceil(filteredPolicies.value.length / itemsPerPage))

const paginatedPolicies = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredPolicies.value.slice(start, end)
})

const visiblePages = computed(() => {
  const pages = []
  const total = totalPages.value
  const current = currentPage.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) {
      pages.push(i)
    }
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) {
        pages.push(i)
      }
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) {
        pages.push(i)
      }
      pages.push('...')
      pages.push(total)
    }
  }
  
  return pages
})

// 方法
const loadPolicies = async () => {
  loading.value = true
  
  try {
    const response = await policyAPI.getPolicyList({
      search: searchQuery.value,
      status: statusFilter.value,
      type: typeFilter.value,
      page: currentPage.value,
      limit: itemsPerPage
    })
    
    policies.value = response.data.items
    stats.value = response.data.stats
  } catch (error) {
    console.error('加载策略列表失败:', error)
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

const formatDateTime = (dateString: string) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date)
}

const resetFilters = () => {
  searchQuery.value = ''
  statusFilter.value = ''
  typeFilter.value = ''
  currentPage.value = 1
}

const viewPolicyDetail = (policy: any) => {
  selectedPolicyId.value = policy.id
  showDetailModal.value = true
}

const activatePolicy = async (policy: any) => {
  try {
    await policyAPI.updatePolicyStatus(policy.id, 'active')
    await loadPolicies()
    alert('策略已启用')
  } catch (error) {
    console.error('启用策略失败:', error)
    alert('启用策略失败，请重试')
  }
}

const deactivatePolicy = async (policy: any) => {
  try {
    await policyAPI.updatePolicyStatus(policy.id, 'inactive')
    await loadPolicies()
    alert('策略已禁用')
  } catch (error) {
    console.error('禁用策略失败:', error)
    alert('禁用策略失败，请重试')
  }
}

const deletePolicy = async (policy: any) => {
  if (!confirm('确定要删除此策略吗？此操作不可恢复。')) {
    return
  }
  
  try {
    await policyAPI.deletePolicy(policy.id)
    await loadPolicies()
    alert('策略已删除')
  } catch (error) {
    console.error('删除策略失败:', error)
    alert('删除策略失败，请重试')
  }
}

const handlePolicyCreated = () => {
  showCreateModal.value = false
  loadPolicies()
}

const handlePolicyUpdated = () => {
  showDetailModal.value = false
  loadPolicies()
}

const handlePolicyDeleted = () => {
  showDetailModal.value = false
  loadPolicies()
}

// 监听器
watch([searchQuery, statusFilter, typeFilter], () => {
  currentPage.value = 1
  loadPolicies()
})

watch(currentPage, () => {
  loadPolicies()
})

// 生命周期
onMounted(() => {
  loadPolicies()
})
</script>

<style lang="scss" scoped>
.policy-management {
  padding: 24px;
  min-height: 100vh;
  background-color: #f8fafc;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.header-content {
  .page-title {
    font-size: 28px;
    font-weight: 700;
    margin: 0 0 8px 0;
  }
  
  .page-description {
    font-size: 16px;
    margin: 0;
    opacity: 0.9;
  }
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.search-bar {
  margin-bottom: 20px;
}

.search-input-wrapper {
  position: relative;
  max-width: 400px;
  
  .search-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #9ca3af;
  }
  
  .search-input {
    width: 100%;
    padding: 12px 12px 12px 44px;
    border: 1px solid #e5e7eb;
    border-radius: 8px;
    font-size: 14px;
    outline: none;
    transition: all 0.2s ease;
    
    &:focus {
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
  }
}

.filter-bar {
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.filter-label {
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  cursor: pointer;
  
  &:focus {
    border-color: #3b82f6;
  }
}

.reset-filter-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
    border-color: #9ca3af;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 32px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
  
  &:hover {
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transform: translateY(-1px);
  }
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #3b82f6;
}

.stat-content {
  .stat-value {
    font-size: 24px;
    font-weight: 700;
    color: #1f2937;
    margin: 0 0 4px 0;
  }
  
  .stat-label {
    font-size: 14px;
    color: #6b7280;
    margin: 0;
  }
}

.policy-list {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-bottom: 24px;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
  
  .loading-text {
    margin-top: 16px;
    color: #6b7280;
  }
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  
  .empty-icon {
    color: #d1d5db;
    margin-bottom: 24px;
  }
  
  .empty-title {
    font-size: 20px;
    font-weight: 600;
    color: #374151;
    margin: 0 0 8px 0;
  }
  
  .empty-description {
    font-size: 14px;
    color: #6b7280;
    margin: 0 0 24px 0;
  }
}

.policies-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.policy-card {
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.2s ease;
  
  &:hover {
    border-color: #3b82f6;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  }
}

.policy-header {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
}

.policy-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  color: #6b7280;
  flex-shrink: 0;
  
  &.type-sync {
    background-color: #dbeafe;
    color: #3b82f6;
  }
  
  &.type-share {
    background-color: #fef3c7;
    color: #d97706;
  }
  
  &.type-backup {
    background-color: #dcfce7;
    color: #10b981;
  }
  
  &.type-migration {
    background-color: #fecaca;
    color: #ef4444;
  }
  
  img {
    width: 20px;
    height: 20px;
  }
}

.policy-info {
  flex: 1;
  
  .policy-name {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin: 0 0 4px 0;
  }
  
  .policy-description {
    font-size: 12px;
    color: #6b7280;
    margin: 0;
    line-height: 1.4;
  }
}

.policy-status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  flex-shrink: 0;
  
  &.status-active {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.status-inactive {
    background-color: #f3f4f6;
    color: #6b7280;
  }
  
  &.status-draft {
    background-color: #fef3c7;
    color: #92400e;
  }
}

.policy-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-label {
  font-size: 12px;
  color: #6b7280;
}

.detail-value {
  font-size: 12px;
  color: #1f2937;
  font-weight: 500;
  text-align: right;
}

.policy-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  font-size: 12px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
  }
  
  &.primary {
    background-color: #3b82f6;
    border-color: #3b82f6;
    color: white;
    
    &:hover {
      background-color: #2563eb;
    }
  }
  
  &.secondary {
    background-color: #6b7280;
    border-color: #6b7280;
    color: white;
    
    &:hover {
      background-color: #4b5563;
    }
  }
  
  &.danger {
    background-color: #ef4444;
    border-color: #ef4444;
    color: white;
    
    &:hover {
      background-color: #dc2626;
    }
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 32px;
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover:not(:disabled) {
    background-color: #f9fafb;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.page-numbers {
  display: flex;
  gap: 4px;
}

.page-number {
  width: 36px;
  height: 36px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
  }
  
  &.active {
    background-color: #3b82f6;
    border-color: #3b82f6;
    color: white;
  }
}

@media (max-width: 768px) {
  .policy-management {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    text-align: left;
  }
  
  .header-actions {
    align-self: stretch;
    justify-content: stretch;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .filter-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .policy-list {
    padding: 16px;
  }
  
  .policies-grid {
    grid-template-columns: 1fr;
  }
  
  .policy-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .policy-actions {
    flex-direction: column-reverse;
  }
  
  .action-btn {
    width: 100%;
  }
  
  .pagination {
    flex-direction: column;
    gap: 12px;
  }
  
  .page-numbers {
    order: -1;
  }
}
</style>