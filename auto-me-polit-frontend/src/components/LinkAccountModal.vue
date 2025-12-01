<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">关联第三方账户</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <div v-if="step === 'platform'" class="platform-selection">
          <p class="section-description">选择要关联的第三方平台</p>
          <div class="platform-grid">
            <div
              v-for="platform in availablePlatforms"
              :key="platform.id"
              class="platform-option"
              @click="selectPlatform(platform)"
            >
              <div class="platform-icon" :class="`platform-${platform.id}`">
                <img v-if="platform.icon" :src="platform.icon" :alt="platform.name" />
                <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
                </svg>
              </div>
              <div class="platform-info">
                <h3 class="platform-name">{{ platform.name }}</h3>
                <p class="platform-description">{{ platform.description }}</p>
              </div>
              <div class="platform-arrow">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M8.59 16.59L13.17 12 8.59 7.41 10 6l6 6-6 6-1.41-1.41z"/>
                </svg>
              </div>
            </div>
          </div>
        </div>
        
        <div v-else-if="step === 'auth'" class="auth-process">
          <div class="selected-platform">
            <div class="platform-icon" :class="`platform-${selectedPlatform.id}`">
              <img v-if="selectedPlatform.icon" :src="selectedPlatform.icon" :alt="selectedPlatform.name" />
              <svg v-else width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
              </svg>
            </div>
            <h3 class="platform-name">{{ selectedPlatform.name }}</h3>
          </div>
          
          <div v-if="loading" class="auth-loading">
            <div class="loading-spinner"></div>
            <p class="loading-text">正在跳转到 {{ selectedPlatform.name }} 授权...</p>
          </div>
          
          <div v-else class="auth-form">
            <form @submit.prevent="handleAuthSubmit">
              <div class="form-group">
                <label class="form-label">授权说明</label>
                <div class="auth-description">
                  <p>我们将通过 {{ selectedPlatform.name }} 授权您的账户信息，用于：</p>
                  <ul>
                    <li>获取您的基本账户信息</li>
                    <li>实现账户关联和身份验证</li>
                    <li>提供跨平台的数据同步服务</li>
                  </ul>
                  <p class="auth-warning">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                      <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
                    </svg>
                    我们不会存储您的密码，仅获取必要的身份验证信息。
                  </p>
                </div>
              </div>
              
              <div class="form-group">
                <label class="form-label">关联配置</label>
                <div class="checkbox-group">
                  <label class="checkbox-item">
                    <input type="checkbox" v-model="config.autoSync" />
                    <span class="checkbox-mark"></span>
                    <span class="checkbox-label">自动同步数据</span>
                  </label>
                  <label class="checkbox-item">
                    <input type="checkbox" v-model="config.enableNotifications" />
                    <span class="checkbox-mark"></span>
                    <span class="checkbox-label">启用通知</span>
                  </label>
                  <label class="checkbox-item">
                    <input type="checkbox" v-model="config.shareProfile" />
                    <span class="checkbox-mark"></span>
                    <span class="checkbox-label">共享个人资料</span>
                  </label>
                </div>
              </div>
              
              <div class="form-actions">
                <BaseButton
                  type="button"
                  variant="secondary"
                  @click="step = 'platform'"
                >
                  返回
                </BaseButton>
                <BaseButton
                  type="submit"
                  variant="primary"
                  :loading="loading"
                >
                  开始授权
                </BaseButton>
              </div>
            </form>
          </div>
        </div>
        
        <div v-else-if="step === 'complete'" class="auth-complete">
          <div class="success-icon">
            <svg width="64" height="64" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
          </div>
          <h3 class="success-title">账户关联成功！</h3>
          <p class="success-description">
            您已成功将 {{ selectedPlatform.name }} 账户关联到系统中。
          </p>
          
          <div class="success-actions">
            <BaseButton
              variant="secondary"
              @click="resetAndClose"
            >
              继续添加
            </BaseButton>
            <BaseButton
              variant="primary"
              @click="$emit('linked')"
            >
              完成
            </BaseButton>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { accountAPI } from '@/api/modules/account'
import BaseButton from '@/components/base/Button.vue'

// 响应式数据
const step = ref<'platform' | 'auth' | 'complete'>('platform')
const loading = ref(false)
const selectedPlatform = ref<any>(null)

const availablePlatforms = [
  {
    id: 'wechat',
    name: '微信',
    description: '关联微信账户，实现微信身份验证',
    icon: '/icons/wechat.png'
  },
  {
    id: 'alipay',
    name: '支付宝',
    description: '关联支付宝账户，享受便捷服务',
    icon: '/icons/alipay.png'
  },
  {
    id: 'weibo',
    name: '微博',
    description: '关联微博账户，扩展社交身份',
    icon: '/icons/weibo.png'
  },
  {
    id: 'qq',
    name: 'QQ',
    description: '关联QQ账户，连接更多服务',
    icon: '/icons/qq.png'
  },
  {
    id: 'douyin',
    name: '抖音',
    description: '关联抖音账户，管理短视频身份',
    icon: '/icons/douyin.png'
  },
  {
    id: 'github',
    name: 'GitHub',
    description: '关联GitHub账户，管理开发者身份',
    icon: '/icons/github.png'
  },
  {
    id: 'google',
    name: 'Google',
    description: '关联Google账户，扩展国际服务',
    icon: '/icons/google.png'
  }
]

const config = reactive({
  autoSync: true,
  enableNotifications: true,
  shareProfile: false
})

// 方法
const selectPlatform = (platform: any) => {
  selectedPlatform.value = platform
  step.value = 'auth'
}

const handleAuthSubmit = async () => {
  loading.value = true
  
  try {
    // 模拟授权流程
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 这里应该调用实际的关联API
    const requestData = {
      platform: selectedPlatform.value.id,
      config: { ...config },
      timestamp: new Date().toISOString()
    }
    
    await accountAPI.linkAccount(requestData)
    
    step.value = 'complete'
  } catch (error) {
    console.error('关联账户失败:', error)
    alert('关联失败，请重试')
    step.value = 'platform'
  } finally {
    loading.value = false
  }
}

const resetAndClose = () => {
  step.value = 'platform'
  selectedPlatform.value = null
  config.autoSync = true
  config.enableNotifications = true
  config.shareProfile = false
}

const $emit = defineEmits<{
  close: []
  linked: []
}>()
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

.section-description {
  color: #6b7280;
  margin-bottom: 24px;
  text-align: center;
}

.platform-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.platform-option {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    border-color: #3b82f6;
    background-color: #f8faff;
  }
}

.platform-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  background-color: #6b7280;
  flex-shrink: 0;
  
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
    width: 32px;
    height: 32px;
  }
  
  svg {
    width: 32px;
    height: 32px;
  }
}

.platform-info {
  flex: 1;
}

.platform-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
}

.platform-description {
  font-size: 12px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.platform-arrow {
  color: #9ca3af;
}

.selected-platform {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.auth-loading {
  text-align: center;
  padding: 40px 20px;
}

.loading-text {
  margin-top: 16px;
  color: #6b7280;
}

.auth-description {
  background-color: #f9fafb;
  padding: 16px;
  border-radius: 6px;
  border-left: 4px solid #3b82f6;
  
  p {
    margin: 0 0 12px 0;
    color: #374151;
    line-height: 1.5;
  }
  
  ul {
    margin: 0 0 12px 0;
    padding-left: 20px;
    
    li {
      margin-bottom: 4px;
      color: #6b7280;
    }
  }
}

.auth-warning {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  background-color: #fef3c7;
  color: #92400e;
  padding: 12px;
  border-radius: 6px;
  margin-top: 12px;
  
  svg {
    flex-shrink: 0;
    margin-top: 2px;
  }
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
  
  input[type="checkbox"] {
    display: none;
  }
  
  .checkbox-mark {
    width: 16px;
    height: 16px;
    border: 1px solid #d1d5db;
    border-radius: 2px;
    position: relative;
    transition: all 0.2s ease;
    
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
  
  input[type="checkbox"]:checked + .checkbox-mark {
    background-color: #3b82f6;
    border-color: #3b82f6;
    
    &::after {
      opacity: 1;
    }
  }
  
  .checkbox-label {
    font-size: 14px;
    color: #374151;
  }
}

.form-actions {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.auth-complete {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  color: #10b981;
  margin-bottom: 24px;
}

.success-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
}

.success-description {
  color: #6b7280;
  margin-bottom: 32px;
  line-height: 1.5;
}

.success-actions {
  display: flex;
  justify-content: center;
  gap: 12px;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95vw;
    margin: 20px;
  }
  
  .modal-body {
    padding: 20px;
  }
  
  .platform-grid {
    grid-template-columns: 1fr;
  }
  
  .platform-option {
    padding: 16px;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
  
  .success-actions {
    flex-direction: column-reverse;
  }
}
</style>