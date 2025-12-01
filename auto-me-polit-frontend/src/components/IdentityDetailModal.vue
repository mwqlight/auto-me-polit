<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">身份详情</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <div class="identity-header">
          <div class="identity-avatar">
            <img v-if="identity.avatar" :src="identity.avatar" :alt="identity.name" />
            <div v-else class="avatar-placeholder">
              {{ identity.name.charAt(0).toUpperCase() }}
            </div>
          </div>
          
          <div class="identity-basic-info">
            <h3 class="identity-name">{{ identity.name }}</h3>
            <p v-if="identity.description" class="identity-description">{{ identity.description }}</p>
            <div class="identity-status" :class="`status-${identity.status}`">
              <span class="status-indicator" :class="`status-${identity.status}`"></span>
              {{ getStatusText(identity.status) }}
            </div>
          </div>
        </div>
        
        <div class="identity-details">
          <!-- 标签 -->
          <div v-if="identity.tags && identity.tags.length > 0" class="detail-section">
            <h4 class="section-title">标签</h4>
            <div class="tags-list">
              <span v-for="tag in identity.tags" :key="tag" class="tag">{{ tag }}</span>
            </div>
          </div>
          
          <!-- 设备绑定 -->
          <div class="detail-section">
            <h4 class="section-title">设备绑定</h4>
            <div class="detail-item">
              <span class="detail-label">设备状态</span>
              <span class="detail-value">
                <span class="status-indicator" :class="identity.deviceBound ? 'status-active' : 'status-inactive'"></span>
                {{ identity.deviceBound ? '已绑定' : '未绑定' }}
              </span>
            </div>
            <div v-if="identity.deviceInfo" class="detail-item">
              <span class="detail-label">设备信息</span>
              <span class="detail-value">{{ identity.deviceInfo }}</span>
            </div>
          </div>
          
          <!-- 生物特征 -->
          <div class="detail-section">
            <h4 class="section-title">生物特征</h4>
            <div class="detail-item">
              <span class="detail-label">启用状态</span>
              <span class="detail-value">
                <span class="status-indicator" :class="identity.enableBiometric ? 'status-active' : 'status-inactive'"></span>
                {{ identity.enableBiometric ? '已启用' : '未启用' }}
              </span>
            </div>
            <div v-if="identity.enableBiometric && identity.biometricTypes" class="detail-item">
              <span class="detail-label">特征类型</span>
              <span class="detail-value">
                <span v-if="identity.biometricTypes.fingerprint" class="feature-badge">指纹</span>
                <span v-if="identity.biometricTypes.face" class="feature-badge">面部</span>
                <span v-if="identity.biometricTypes.voice" class="feature-badge">声纹</span>
              </span>
            </div>
          </div>
          
          <!-- 统计信息 -->
          <div class="detail-section">
            <h4 class="section-title">统计信息</h4>
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ identity.deviceCount || 0 }}</div>
                <div class="stat-label">绑定设备</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ identity.accountCount || 0 }}</div>
                <div class="stat-label">关联账户</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ identity.usageCount || 0 }}</div>
                <div class="stat-label">使用次数</div>
              </div>
            </div>
          </div>
          
          <!-- 时间信息 -->
          <div class="detail-section">
            <h4 class="section-title">时间信息</h4>
            <div class="detail-item">
              <span class="detail-label">创建时间</span>
              <span class="detail-value">{{ formatDateTime(identity.createdAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">更新时间</span>
              <span class="detail-value">{{ formatDateTime(identity.updatedAt) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">最后使用</span>
              <span class="detail-value">{{ formatDateTime(identity.lastUsedAt) }}</span>
            </div>
          </div>
        </div>
        
        <div class="modal-actions">
          <BaseButton
            type="button"
            variant="secondary"
            @click="$emit('close')"
          >
            关闭
          </BaseButton>
          <BaseButton
            type="button"
            variant="primary"
            @click="handleEdit"
          >
            编辑
          </BaseButton>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import BaseButton from '@/components/base/Button.vue'

interface Props {
  identity: any
}

const props = defineProps<Props>()

const emit = defineEmits<{
  close: []
  updated: []
}>()

// 方法
const getStatusText = (status: string) => {
  const statusMap = {
    active: '活跃',
    inactive: '非活跃',
    suspended: '已暂停'
  }
  return statusMap[status] || status
}

const formatDateTime = (dateString: string) => {
  if (!dateString) return '-'
  return new Date(dateString).toLocaleString('zh-CN')
}

const handleEdit = () => {
  // 这里应该打开编辑模态框或跳转到编辑页面
  console.log('编辑身份:', props.identity.id)
}
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
  max-width: 600px;
  width: 90vw;
  max-height: 90vh;
  overflow: hidden;
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
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.identity-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.identity-avatar {
  width: 80px;
  height: 80px;
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
    font-size: 24px;
    font-weight: 600;
  }
}

.identity-basic-info {
  flex: 1;
  min-width: 0;
}

.identity-name {
  font-size: 22px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.identity-description {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.identity-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  padding: 4px 12px;
  border-radius: 6px;
  width: fit-content;
  
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

.identity-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  padding-bottom: 4px;
  border-bottom: 1px solid #e5e7eb;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  display: inline-block;
  background-color: #e0e7ff;
  color: #3730a3;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
  font-weight: 500;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 8px 0;
}

.detail-label {
  font-weight: 500;
  color: #374151;
  min-width: 100px;
}

.detail-value {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 6px;
}

.feature-badge {
  display: inline-block;
  background-color: #dbeafe;
  color: #1e40af;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  margin-right: 4px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95vw;
    margin: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .identity-header {
    flex-direction: column;
    text-align: center;
  }
  
  .detail-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-actions {
    flex-direction: column-reverse;
  }
}
</style>