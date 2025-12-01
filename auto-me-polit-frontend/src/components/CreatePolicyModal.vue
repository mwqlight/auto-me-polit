<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">创建共享策略</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            
            <div class="form-group">
              <label class="form-label">策略名称 <span class="required">*</span></label>
              <input
                v-model="form.name"
                type="text"
                class="form-input"
                placeholder="输入策略名称"
                required
              />
              <p class="form-help">为您的策略起一个易于识别的名称</p>
            </div>
            
            <div class="form-group">
              <label class="form-label">策略描述</label>
              <textarea
                v-model="form.description"
                class="form-textarea"
                placeholder="描述该策略的用途和作用"
                rows="3"
              ></textarea>
            </div>
            
            <div class="form-group">
              <label class="form-label">策略类型 <span class="required">*</span></label>
              <div class="radio-group">
                <label
                  v-for="type in policyTypes"
                  :key="type.value"
                  class="radio-option"
                >
                  <input
                    v-model="form.type"
                    type="radio"
                    :value="type.value"
                    class="radio-input"
                  />
                  <div class="radio-mark"></div>
                  <div class="radio-content">
                    <span class="radio-label">{{ type.label }}</span>
                    <span class="radio-description">{{ type.description }}</span>
                  </div>
                </label>
              </div>
            </div>
          </div>
          
          <!-- 账户选择 -->
          <div class="form-section">
            <h3 class="section-title">账户配置</h3>
            
            <div class="form-group">
              <label class="form-label">源账户 <span class="required">*</span></label>
              <div class="account-selector">
                <select v-model="form.sourceAccountId" class="form-select" required>
                  <option value="">请选择源账户</option>
                  <option
                    v-for="account in accounts"
                    :key="account.id"
                    :value="account.id"
                  >
                    {{ account.name }} ({{ getPlatformText(account.platform) }})
                  </option>
                </select>
                <div class="account-preview" v-if="selectedSourceAccount">
                  <div class="account-info">
                    <div class="account-icon" :class="`platform-${selectedSourceAccount.platform}`">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                      </svg>
                    </div>
                    <div class="account-details">
                      <span class="account-name">{{ selectedSourceAccount.name }}</span>
                      <span class="account-status">{{ selectedSourceAccount.status }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">目标账户 <span class="required">*</span></label>
              <div class="account-selector">
                <select v-model="form.targetAccountId" class="form-select" required>
                  <option value="">请选择目标账户</option>
                  <option
                    v-for="account in filteredTargetAccounts"
                    :key="account.id"
                    :value="account.id"
                  >
                    {{ account.name }} ({{ getPlatformText(account.platform) }})
                  </option>
                </select>
                <div class="account-preview" v-if="selectedTargetAccount">
                  <div class="account-info">
                    <div class="account-icon" :class="`platform-${selectedTargetAccount.platform}`">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                      </svg>
                    </div>
                    <div class="account-details">
                      <span class="account-name">{{ selectedTargetAccount.name }}</span>
                      <span class="account-status">{{ selectedTargetAccount.status }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 数据配置 -->
          <div class="form-section">
            <h3 class="section-title">数据配置</h3>
            
            <div class="form-group">
              <label class="form-label">数据类型</label>
              <div class="checkbox-grid">
                <label
                  v-for="dataType in dataTypes"
                  :key="dataType.value"
                  class="checkbox-option"
                >
                  <input
                    v-model="form.dataTypes"
                    type="checkbox"
                    :value="dataType.value"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <div class="checkbox-content">
                    <span class="checkbox-label">{{ dataType.label }}</span>
                    <span class="checkbox-description">{{ dataType.description }}</span>
                  </div>
                </label>
              </div>
            </div>
            
            <div class="form-group">
              <label class="form-label">同步规则</label>
              <div class="sync-rules">
                <label class="checkbox-option">
                  <input
                    v-model="form.syncRules"
                    type="checkbox"
                    value="realTime"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">实时同步</span>
                </label>
                <label class="checkbox-option">
                  <input
                    v-model="form.syncRules"
                    type="checkbox"
                    value="bidirectional"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">双向同步</span>
                </label>
                <label class="checkbox-option">
                  <input
                    v-model="form.syncRules"
                    type="checkbox"
                    value="conflictResolve"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">冲突自动解决</span>
                </label>
              </div>
            </div>
          </div>
          
          <!-- 高级配置 -->
          <div class="form-section">
            <h3 class="section-title">高级配置</h3>
            
            <div class="form-group">
              <label class="form-label">执行频率</label>
              <select v-model="form.executionFrequency" class="form-select">
                <option value="manual">手动执行</option>
                <option value="hourly">每小时</option>
                <option value="daily">每天</option>
                <option value="weekly">每周</option>
                <option value="monthly">每月</option>
              </select>
            </div>
            
            <div class="form-group">
              <label class="form-label">保留策略</label>
              <select v-model="form.retentionPolicy" class="form-select">
                <option value="30days">30天</option>
                <option value="90days">90天</option>
                <option value="1year">1年</option>
                <option value="forever">永久保留</option>
              </select>
            </div>
            
            <div class="form-group">
              <label class="form-label">通知设置</label>
              <div class="notification-settings">
                <label class="checkbox-option">
                  <input
                    v-model="form.notifications.success"
                    type="checkbox"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">成功通知</span>
                </label>
                <label class="checkbox-option">
                  <input
                    v-model="form.notifications.failure"
                    type="checkbox"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">失败通知</span>
                </label>
                <label class="checkbox-option">
                  <input
                    v-model="form.notifications.conflict"
                    type="checkbox"
                    class="checkbox-input"
                  />
                  <div class="checkbox-mark"></div>
                  <span class="checkbox-label">冲突通知</span>
                </label>
              </div>
            </div>
          </div>
          
          <!-- 预览 -->
          <div v-if="canPreview" class="form-section">
            <h3 class="section-title">配置预览</h3>
            <div class="policy-preview">
              <div class="preview-item">
                <span class="preview-label">策略名称</span>
                <span class="preview-value">{{ form.name || '未设置' }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">策略类型</span>
                <span class="preview-value">{{ getTypeText(form.type) }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">数据流向</span>
                <span class="preview-value">{{ getDataFlowText() }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">执行频率</span>
                <span class="preview-value">{{ getFrequencyText(form.executionFrequency) }}</span>
              </div>
              <div class="preview-item">
                <span class="preview-label">数据类型</span>
                <span class="preview-value">{{ getSelectedDataTypes() }}</span>
              </div>
            </div>
          </div>
        </form>
      </div>
      
      <div class="modal-footer">
        <BaseButton
          type="button"
          variant="secondary"
          @click="$emit('close')"
        >
          取消
        </BaseButton>
        <BaseButton
          type="button"
          variant="outline"
          @click="saveAsDraft"
          :loading="saving"
        >
          保存草稿
        </BaseButton>
        <BaseButton
          type="button"
          variant="primary"
          @click="handleSubmit"
          :loading="saving"
        >
          创建策略
        </BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { policyAPI } from '@/api/modules/policy'
import { accountAPI } from '@/api/modules/account'
import BaseButton from '@/components/base/Button.vue'

// 响应式数据
const saving = ref(false)
const accounts = ref<any[]>([])

const form = ref({
  name: '',
  description: '',
  type: 'sync',
  sourceAccountId: '',
  targetAccountId: '',
  dataTypes: ['profile'],
  syncRules: ['realTime'],
  executionFrequency: 'daily',
  retentionPolicy: '90days',
  notifications: {
    success: true,
    failure: true,
    conflict: false
  }
})

const policyTypes = [
  {
    value: 'sync',
    label: '数据同步',
    description: '在不同平台间同步用户数据'
  },
  {
    value: 'share',
    label: '数据共享',
    description: '有选择性地共享特定数据'
  },
  {
    value: 'backup',
    label: '数据备份',
    description: '将重要数据备份到安全账户'
  },
  {
    value: 'migration',
    label: '数据迁移',
    description: '将数据从一个平台迁移到另一个'
  }
]

const dataTypes = [
  {
    value: 'profile',
    label: '个人资料',
    description: '姓名、头像、简介等基础信息'
  },
  {
    value: 'posts',
    label: '帖子内容',
    description: '发布的内容和帖子'
  },
  {
    value: 'contacts',
    label: '联系人',
    description: '好友列表和联系人信息'
  },
  {
    value: 'media',
    label: '媒体文件',
    description: '图片、视频等媒体资源'
  },
  {
    value: 'preferences',
    label: '偏好设置',
    description: '账户偏好和应用设置'
  },
  {
    value: 'activity',
    label: '活动记录',
    description: '登录记录和操作历史'
  }
]

// 计算属性
const selectedSourceAccount = computed(() => {
  return accounts.value.find(acc => acc.id === form.value.sourceAccountId)
})

const selectedTargetAccount = computed(() => {
  return accounts.value.find(acc => acc.id === form.value.targetAccountId)
})

const filteredTargetAccounts = computed(() => {
  return accounts.value.filter(acc => acc.id !== form.value.sourceAccountId)
})

const canPreview = computed(() => {
  return form.value.name && form.value.sourceAccountId && form.value.targetAccountId
})

// 方法
const loadAccounts = async () => {
  try {
    const response = await accountAPI.getLinkedAccounts()
    accounts.value = response.data
  } catch (error) {
    console.error('加载账户列表失败:', error)
  }
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

const getTypeText = (type: string) => {
  const typeMap: Record<string, string> = {
    sync: '数据同步',
    share: '数据共享',
    backup: '数据备份',
    migration: '数据迁移'
  }
  return typeMap[type] || type
}

const getDataFlowText = () => {
  if (!selectedSourceAccount.value || !selectedTargetAccount.value) {
    return '未配置'
  }
  
  const source = selectedSourceAccount.value.name
  const target = selectedTargetAccount.value.name
  
  if (form.value.syncRules.includes('bidirectional')) {
    return `${source} ↔ ${target}`
  } else {
    return `${source} → ${target}`
  }
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

const getSelectedDataTypes = () => {
  if (form.value.dataTypes.length === 0) return '未选择'
  if (form.value.dataTypes.length === dataTypes.length) return '全部'
  return `${form.value.dataTypes.length}项已选择`
}

const validateForm = () => {
  if (!form.value.name.trim()) {
    alert('请输入策略名称')
    return false
  }
  
  if (!form.value.type) {
    alert('请选择策略类型')
    return false
  }
  
  if (!form.value.sourceAccountId) {
    alert('请选择源账户')
    return false
  }
  
  if (!form.value.targetAccountId) {
    alert('请选择目标账户')
    return false
  }
  
  if (form.value.sourceAccountId === form.value.targetAccountId) {
    alert('源账户和目标账户不能相同')
    return false
  }
  
  if (form.value.dataTypes.length === 0) {
    alert('请至少选择一种数据类型')
    return false
  }
  
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return
  
  saving.value = true
  
  try {
    const requestData = {
      ...form.value,
      status: 'active'
    }
    
    await policyAPI.createPolicy(requestData)
    
    $emit('created')
  } catch (error) {
    console.error('创建策略失败:', error)
    alert('创建策略失败，请重试')
  } finally {
    saving.value = false
  }
}

const saveAsDraft = async () => {
  if (!form.value.name.trim()) {
    alert('请输入策略名称')
    return
  }
  
  saving.value = true
  
  try {
    const requestData = {
      ...form.value,
      status: 'draft'
    }
    
    await policyAPI.createPolicy(requestData)
    
    $emit('created')
  } catch (error) {
    console.error('保存草稿失败:', error)
    alert('保存草稿失败，请重试')
  } finally {
    saving.value = false
  }
}

const $emit = defineEmits<{
  close: []
  created: []
}>()

// 生命周期
loadAccounts()
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

.form-section {
  margin-bottom: 32px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e5e7eb;
}

.form-group {
  margin-bottom: 20px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #374151;
  margin-bottom: 6px;
}

.required {
  color: #ef4444;
}

.form-help {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: all 0.2s ease;
  
  &:focus {
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  }
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.form-select {
  cursor: pointer;
}

.radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.radio-option {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  cursor: pointer;
  user-select: none;
  
  .radio-input {
    display: none;
  }
  
  .radio-mark {
    width: 16px;
    height: 16px;
    border: 1px solid #d1d5db;
    border-radius: 50%;
    position: relative;
    transition: all 0.2s ease;
    flex-shrink: 0;
    margin-top: 2px;
    
    &::after {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background-color: white;
      transform: translate(-50%, -50%) scale(0);
      transition: transform 0.2s ease;
    }
  }
  
  .radio-input:checked + .radio-mark {
    background-color: #3b82f6;
    border-color: #3b82f6;
    
    &::after {
      transform: translate(-50%, -50%) scale(1);
    }
  }
  
  .radio-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
  
  .radio-label {
    font-size: 14px;
    font-weight: 500;
    color: #1f2937;
  }
  
  .radio-description {
    font-size: 12px;
    color: #6b7280;
    line-height: 1.4;
  }
}

.account-selector {
  .form-select {
    margin-bottom: 8px;
  }
}

.account-preview {
  .account-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px 12px;
    background-color: #f9fafb;
    border-radius: 6px;
  }
  
  .account-icon {
    width: 24px;
    height: 24px;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #e5e7eb;
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
  }
  
  .account-details {
    display: flex;
    flex-direction: column;
    gap: 2px;
  }
  
  .account-name {
    font-size: 12px;
    font-weight: 500;
    color: #1f2937;
  }
  
  .account-status {
    font-size: 10px;
    color: #6b7280;
  }
}

.checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
}

.checkbox-option {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  user-select: none;
  
  .checkbox-input {
    display: none;
  }
  
  .checkbox-mark {
    width: 16px;
    height: 16px;
    border: 1px solid #d1d5db;
    border-radius: 2px;
    position: relative;
    transition: all 0.2s ease;
    flex-shrink: 0;
    margin-top: 2px;
    
    &::after {
      content: '';
      position: absolute;
      top: 2px;
      left: 5px;
      width: 4px;
      height: 8px;
      border: solid white;
      border-width: 0 1px 1px 0;
      transform: rotate(45deg);
      opacity: 0;
      transition: opacity 0.2s ease;
    }
  }
  
  .checkbox-input:checked + .checkbox-mark {
    background-color: #3b82f6;
    border-color: #3b82f6;
    
    &::after {
      opacity: 1;
    }
  }
  
  .checkbox-content {
    display: flex;
    flex-direction: column;
    gap: 2px;
    flex: 1;
  }
  
  .checkbox-label {
    font-size: 14px;
    font-weight: 500;
    color: #1f2937;
  }
  
  .checkbox-description {
    font-size: 12px;
    color: #6b7280;
    line-height: 1.4;
  }
  
  .checkbox-label {
    font-size: 14px;
    color: #374151;
  }
}

.sync-rules,
.notification-settings {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.policy-preview {
  background-color: #f9fafb;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e5e7eb;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #e5e7eb;
  
  &:last-child {
    border-bottom: none;
  }
}

.preview-label {
  font-size: 12px;
  font-weight: 500;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.preview-value {
  font-size: 12px;
  color: #1f2937;
  font-weight: 500;
  text-align: right;
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
  
  .checkbox-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-footer {
    flex-direction: column-reverse;
  }
}
</style>