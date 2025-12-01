<template>
  <div class="account-page">
    <div class="page-header">
      <h1 class="text-2xl font-bold">账户关联</h1>
      <div class="header-actions">
        <BaseButton @click="showLinkModal = true" variant="primary">
          <template #default>
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
            </svg>
            关联账户
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
              placeholder="搜索账户名称或类型"
              @input="handleSearch"
            />
          </div>
          <div class="form-group" style="width: 150px;">
            <label class="form-label">平台类型</label>
            <select v-model="platformFilter" class="form-input" @change="handleFilter">
              <option value="">全部</option>
              <option value="wechat">微信</option>
              <option value="alipay">支付宝</option>
              <option value="weibo">微博</option>
              <option value="qq">QQ</option>
              <option value="douyin">抖音</option>
              <option value="github">GitHub</option>
              <option value="google">Google</option>
              <option value="other">其他</option>
            </select>
          </div>
          <div class="form-group" style="width: 150px;">
            <label class="form-label">状态</label>
            <select v-model="statusFilter" class="form-input" @change="handleFilter">
              <option value="">全部</option>
              <option value="connected">已连接</option>
              <option value="disconnected">已断开</option>
              <option value="expired">已过期</option>
              <option value="error">错误</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- 账户列表 -->
    <div class="card">
      <div class="card-body">
        <div v-if="loading" class="text-center py-8">
          <div class="loading-spinner"></div>
          <p class="text-muted mt-2">加载中...</p>
        </div>
        
        <div v-else-if="filteredAccounts.length === 0" class="text-center py-8">
          <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor" class="text-muted mx-auto mb-4">
            <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z"/>
          </svg>
          <h3 class="text-lg font-medium text-muted mb-2">暂无关联账户</h3>
          <p class="text-muted mb-4">您还没有关联任何第三方账户</p>
          <BaseButton @click="showLinkModal = true" variant="primary">
            关联第一个账户
          </BaseButton>
        </div>
        
        <div v-else class="account-grid">
          <div
            v-for="account in filteredAccounts"
            :key="account.id"
            class="account-card"
            @click="selectAccount(account)"
          >
            <div class="account-header">
              <div class="account-platform" :class="`platform-${account.platform}`">
                <img v-if="getPlatformIcon(account.platform)" :src="getPlatformIcon(account.platform)" :alt="account.platform" />
                <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <div class="account-status" :class="`status-${account.status}`">
                <span class="status-indicator" :class="`status-${account.status}`"></span>
                {{ getStatusText(account.status) }}
              </div>
            </div>
            
            <div class="account-info">
              <h3 class="account-name">{{ account.displayName }}</h3>
              <p class="account-type">{{ getPlatformText(account.platform) }}</p>
              <p v-if="account.description" class="account-description">{{ account.description }}</p>
            </div>
            
            <div class="account-meta">
              <div class="meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M9 11H7v2h2v-2zm4 0h-2v2h2v-2zm4 0h-2v2h2v-2zm2-7h-1V2h-2v2H8V2H6v2H5c-1.1 0-1.99.9-1.99 2L3 20c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 16H5V9h14v11z"/>
                </svg>
                关联时间: {{ formatDate(account.createdAt) }}
              </div>
              <div class="meta-item">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M11 18h2v-2h-2v2zm1-16C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm0-14c-2.21 0-4 1.79-4 4h2c0-1.1.9-2 2-2s2 .9 2 2c0 2-3 1.75-3 5h2c0-2.25 3-2.5 3-5 0-2.21-1.79-4-4-4z"/>
                </svg>
                最后同步: {{ formatDate(account.lastSyncAt) }}
              </div>
            </div>
            
            <div class="account-actions">
              <button class="action-btn" @click.stop="syncAccount(account)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 6V3L8 7l4 4V8c2.76 0 5 2.24 5 5 0 1.01-.25 1.97-.7 2.8l1.46 1.46C19.54 15.53 20 14.33 20 13c0-2.21-1.79-4-4-4zM4.7 18.54C4.25 17.71 4 16.89 4 16c0-2.21 1.79-4 4-4v3l4-4-4-4v3c-2.76 0-5 2.24-5 5 0 1.33.46 2.54 1.3 3.54L4.7 18.54z"/>
                </svg>
                同步
              </button>
              <button class="action-btn" @click.stop="validateAccount(account)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                </svg>
                验证
              </button>
              <button class="action-btn" @click.stop="disconnectAccount(account)">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M13 3h-2v9h6v-2h-4V3zM5 5h6v2H7.82l5.18 5.18-1.41 1.41L5 7.41V5z"/>
                </svg>
                断开
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

    <!-- 关联账户模态框 -->
    <LinkAccountModal
      v-if="showLinkModal"
      @close="showLinkModal = false"
      @linked="handleAccountLinked"
    />

    <!-- 账户详情模态框 -->
    <AccountDetailModal
      v-if="selectedAccount"
      :account="selectedAccount"
      @close="selectedAccount = null"
      @updated="handleAccountUpdated"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { accountAPI } from '@/api/modules/account'
import BaseButton from '@/components/base/Button.vue'

// 响应式数据
const loading = ref(false)
const accounts = ref([])
const searchQuery = ref('')
const platformFilter = ref('')
const statusFilter = ref('')
const showLinkModal = ref(false)
const selectedAccount = ref(null)

const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 计算属性
const filteredAccounts = computed(() => {
  let filtered = accounts.value
  
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter((account: any) =>
      account.displayName.toLowerCase().includes(query) ||
      account.platform.toLowerCase().includes(query) ||
      (account.description && account.description.toLowerCase().includes(query))
    )
  }
  
  if (platformFilter.value) {
    filtered = filtered.filter((account: any) => account.platform === platformFilter.value)
  }
  
  if (statusFilter.value) {
    filtered = filtered.filter((account: any) => account.status === statusFilter.value)
  }
  
  return filtered
})

const totalPages = computed(() => Math.ceil(total.value / pageSize.value))

// 方法
const loadAccounts = async () => {
  loading.value = true
  try {
    const response = await accountAPI.listLinkedAccounts(currentPage.value, pageSize.value)
    accounts.value = response.data.items || []
    total.value = response.data.total || 0
  } catch (error) {
    console.error('加载账户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadAccounts()
}

const handleFilter = () => {
  currentPage.value = 1
  loadAccounts()
}

const selectAccount = (account: any) => {
  selectedAccount.value = account
}

const syncAccount = async (account: any) => {
  try {
    await accountAPI.syncAccount(account.id)
    await loadAccounts()
  } catch (error) {
    console.error('同步账户失败:', error)
    alert('同步失败，请重试')
  }
}

const validateAccount = async (account: any) => {
  try {
    await accountAPI.validateAccount(account.id)
    await loadAccounts()
  } catch (error) {
    console.error('验证账户失败:', error)
    alert('验证失败，请重试')
  }
}

const disconnectAccount = async (account: any) => {
  if (confirm(`确定要断开账户 "${account.displayName}" 的关联吗？`)) {
    try {
      await accountAPI.disconnectAccount(account.id)
      await loadAccounts()
    } catch (error) {
      console.error('断开账户失败:', error)
      alert('断开失败，请重试')
    }
  }
}

const changePage = (page: number) => {
  currentPage.value = page
  loadAccounts()
}

const getPlatformIcon = (platform: string) => {
  // 这里可以返回对应的图标URL或组件
  const iconMap = {
    wechat: '/icons/wechat.png',
    alipay: '/icons/alipay.png',
    weibo: '/icons/weibo.png',
    qq: '/icons/qq.png',
    douyin: '/icons/douyin.png',
    github: '/icons/github.png',
    google: '/icons/google.png'
  }
  return iconMap[platform as keyof typeof iconMap] || null
}

const getPlatformText = (platform: string) => {
  const platformMap = {
    wechat: '微信',
    alipay: '支付宝',
    weibo: '微博',
    qq: 'QQ',
    douyin: '抖音',
    github: 'GitHub',
    google: 'Google',
    other: '其他'
  }
  return platformMap[platform as keyof typeof platformMap] || platform
}

const getStatusText = (status: string) => {
  const statusMap = {
    connected: '已连接',
    disconnected: '已断开',
    expired: '已过期',
    error: '错误'
  }
  return statusMap[status as keyof typeof statusMap] || status
}

const formatDate = (dateString: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleDateString('zh-CN')
}

const handleAccountLinked = () => {
  showLinkModal.value = false
  loadAccounts()
}

const handleAccountUpdated = () => {
  selectedAccount.value = null
  loadAccounts()
}

// 生命周期
onMounted(() => {
  loadAccounts()
})
</script>

<style lang="scss" scoped>
.account-page {
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

.account-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
}

.account-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
  
  &:hover {
    background-color: #f9fafb;
    border-color: #d1d5db;
    transform: translateY(-2px);
    box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1), 0 2px 4px -2px rgb(0 0 0 / 0.1);
  }
}

.account-header {
  display: flex;
  align-items: center;
  justify-content: between;
  margin-bottom: 16px;
}

.account-platform {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: #6b7280;
  
  &.platform-wechat {
    background-color: #07c160;
  }
  
  &.platform-alipay {
    background-color: #00a0e9;
  }
  
  &.platform-weibo {
    background-color: #e6162d;
  }
  
  &.platform-qq {
    background-color: #12b7f5;
  }
  
  &.platform-douyin {
    background-color: #000000;
  }
  
  &.platform-github {
    background-color: #333333;
  }
  
  &.platform-google {
    background-color: #4285f4;
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

.account-status {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
  margin-left: auto;
  
  &.status-connected {
    background-color: #dcfce7;
    color: #166534;
  }
  
  &.status-disconnected {
    background-color: #f3f4f6;
    color: #6b7280;
  }
  
  &.status-expired {
    background-color: #fef3c7;
    color: #92400e;
  }
  
  &.status-error {
    background-color: #fecaca;
    color: #991b1b;
  }
}

.account-info {
  margin-bottom: 16px;
}

.account-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.account-type {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 4px 0;
}

.account-description {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
  line-height: 1.4;
}

.account-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
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

.account-actions {
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.account-card:hover .account-actions {
  opacity: 1;
}

.action-btn {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  background: white;
  color: #6b7280;
  cursor: pointer;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  transition: all 0.2s ease;
  
  &:hover {
    background-color: #f9fafb;
    color: #374151;
  }
  
  svg {
    width: 14px;
    height: 14px;
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
  .account-page {
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
  
  .account-grid {
    grid-template-columns: 1fr;
  }
  
  .account-card {
    padding: 16px;
  }
  
  .account-actions {
    opacity: 1;
  }
  
  .action-btn {
    flex: 1;
  }
}
</style>