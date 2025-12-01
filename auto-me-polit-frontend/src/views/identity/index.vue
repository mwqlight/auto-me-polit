<template>
  <div class="identity-page">
    <div class="page-header">
      <h1 class="text-2xl font-bold">身份管理</h1>
      <div class="header-actions">
        <BaseButton @click="showCreateModal = true" variant="primary">
          <template #default>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            创建身份
          </template>
        </BaseButton>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <div class="card mb-4">
      <div class="card-body">
        <div class="flex flex-wrap gap-4">
          <div class="form-group" style="flex: 1; min-width: 200px;">
            <label class="form-label">搜索</label>
            <input
              v-model="searchQuery"
              type="text"
              class="form-input"
              placeholder="搜索身份名称或描述"
              @input="handleSearch"
            />
          </div>
          <div class="form-group" style="width: 150px;">
            <label class="form-label">状态</label>
            <select v-model="statusFilter" class="form-input" @change="handleFilter">
              <option value="">全部</option>
              <option value="active">活跃</option>
              <option value="inactive">非活跃</option>
              <option value="suspended">已暂停</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- 身份列表 -->
    <div class="card">
      <div class="card-body">
        <div v-if="loading" class="text-center py-8">
          <div class="loading-spinner"></div>
          <p class="text-muted mt-2">加载中...</p>
        </div>
        
        <div v-else-if="filteredIdentities.length === 0" class="text-center py-8">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor" class="text-muted mx-auto mb-4">
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
          <h3 class="text-lg font-medium text-muted mb-2">暂无身份数据</h3>
          <p class="text-muted mb-4">您还没有创建任何数字身份</p>
          <BaseButton @click="showCreateModal = true" variant="primary">
            创建第一个身份
          </BaseButton>
        </div>
        
        <div v-else class="identity-list">
          <div
            v-for="identity in filteredIdentities"
            :key="identity.id"
            class="identity-item"
            @click="selectIdentity(identity)"
          >
            <div class="identity-avatar">
              <img v-if="identity.avatar" :src="identity.avatar" :alt="identity.name" />
              <div v-else class="avatar-placeholder">
                {{ identity.name.charAt(0).toUpperCase() }}
              </div>
            </div>
            
            <div class="identity-info">
              <div class="identity-header">
                <h3 class="identity-name">{{ identity.name }}</h3>
                <div class="identity-status" :class="`status-${identity.status}`">
                  <span class="status-indicator" :class="`status-${identity.status}`"></span>
                  {{ getStatusText(identity.status) }}
                </div>
              </div>
              
              <p class="identity-description" v-if="identity.description">
                {{ identity.description }}
              </p>
              
              <div class="identity-meta">
                <span class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                  </svg>
                  设备绑定: {{ identity.deviceCount || 0 }}
                </span>
                <span class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
                  </svg>
                  关联账户: {{ identity.accountCount || 0 }}
                </span>
                <span class="meta-item">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.1 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/>
                  </svg>
                  创建时间: {{ formatDate(identity.createdAt) }}
                </span>
              </div>
            </div>
            
            <div class="identity-actions">
              <button class="action-btn" @click.stop="editIdentity(identity)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
                </svg>
              </button>
              <button class="action-btn" @click.stop="deleteIdentity(identity)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div v-if="totalPages > 1" class="pagination">
          <button
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
            class="page-btn"
          >
            上一页
          </button>
          
          <span class="page-info">
            第 {{ currentPage }} 页，共 {{ totalPages }} 页
          </span>
          
          <button
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
            class="page-btn"
          >
            下一页
          </button>
        </div>
      </div>
    </div>

    <!-- 创建身份模态框 -->
    <CreateIdentityModal
      v-if="showCreateModal"
      @close="showCreateModal = false"
      @created="handleIdentityCreated"
    />

    <!-- 身份详情模态框 -->
    <IdentityDetailModal
      v-if="selectedIdentity"
      :identity="selectedIdentity"
      @close="selectedIdentity = null"
      @updated="handleIdentityUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { identityAPI } from '@/api/modules/identity'
import BaseButton from '@/components/base/Button.vue'

// 响应式数据
const loading = ref(false)
const identities = ref([])
const searchQuery = ref('')
const statusFilter = ref('')
const showCreateModal = ref(false)
const selectedIdentity = ref(null)

const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 计算属性
const filteredIdentities = computed(() => {
  let filtered = identities.value
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter((identity: any) =>
      identity.name.toLowerCase().includes(query) ||
      (identity.description && identity.description.toLowerCase().includes(query))
    )
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter((identity: any) => identity.status === statusFilter.value)
  }
  
  return filtered
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 方法
const loadIdentities = async () => {
  loading.value = true
  try {
    const response = await identityAPI.listIdentities(currentPage.value, pageSize.value)
    identities.value = response.data.items || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('加载身份列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  // 重新加载数据
  loadIdentities()
}

const handleFilter = () => {
  currentPage.value = 1
  // 重新加载数据
  loadIdentities()
}

const selectIdentity = (identity: any) => {
  selectedIdentity.value = identity
}

const editIdentity = (identity: any) => {
  selectedIdentity.value = identity
}

const deleteIdentity = async (identity: any) => {
  if (confirm(`确定要删除身份 "${identity.name}" 吗？`)) {
    try {
      await identityAPI.deleteIdentity(identity.id)
      await loadIdentities()
    } catch (error) {
      console.error('删除身份失败:', error)
      alert('删除失败，请重试')
    }
  }
}

const changePage = (page: number) => {
  currentPage.value = page
  loadIdentities()
}

const getStatusText = (status: string) => {
  const statusMap = {
    active: '活跃',
    inactive: '非活跃',
    suspended: '已暂停'
  }
  return statusMap[status] || status
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('zh-CN')
}

const handleIdentityCreated = () => {
  showCreateModal.value = false
  loadIdentities()
}

const handleIdentityUpdated = () => {
  selectedIdentity.value = null
  loadIdentities()
}

// 生命周期
onMounted(() => {
  loadIdentities()
})
</script>

<style lang="scss" scoped>
.identity-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: between;
  align-items: center;
  margin-bottom: 24px;
  
  .header-actions {
    margin-left: auto;
  }
}

.identity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.identity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
    border-color: #d1d5db;
  }
}

.identity-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .avatar-placeholder {
    width: 100%;
    height: 100%;
    background-color: #3b82f6;
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    font-weight: 600;
  }
}

.identity-info {
  flex: 1;
  min-width: 0;
}

.identity-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.identity-name {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.identity-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  
  &.status-active {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.status-inactive {
    background-color: #f3f4f6;
    color: #6b7280;
  }
  
  &.status-suspended {
    background-color: #fef3c7;
    color: #92400e;
  }
}

.identity-description {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.identity-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #6b7280;
  
  svg {
    flex-shrink: 0;
  }
}

.identity-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.identity-item:hover .identity-actions {
  opacity: 1;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: white;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
    color: #374151;
  }
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: white;
  color: #374151;
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

.page-info {
  font-size: 14px;
  color: #6b7280;
}

@media (max-width: 768px) {
  .identity-page {
    padding: 16px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
    
    .header-actions {
      margin-left: 0;
    }
  }
  
  .identity-item {
    padding: 16px;
  }
  
  .identity-avatar {
    width: 48px;
    height: 48px;
  }
  
  .identity-header {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .identity-meta {
    flex-direction: column;
    gap: 8px;
  }
  
  .identity-actions {
    opacity: 1;
  }
}
</style>