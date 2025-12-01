<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2 class="text-xl font-semibold">创建数字身份</h2>
        <button class="close-btn" @click="$emit('close')">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
            <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
          </svg>
        </button>
      </div>
      
      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label class="form-label">身份名称 *</label>
            <input
              v-model="form.name"
              type="text"
              class="form-input"
              :class="{ error: errors.name }"
              placeholder="请输入身份名称"
              required
            />
            <div v-if="errors.name" class="form-error">{{ errors.name }}</div>
          </div>
          
          <div class="form-group">
            <label class="form-label">身份描述</label>
            <textarea
              v-model="form.description"
              class="form-input"
              rows="3"
              placeholder="请输入身份描述"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">头像</label>
            <div class="avatar-upload">
              <div v-if="form.avatar" class="avatar-preview">
                <img :src="form.avatar" alt="头像预览" />
              </div>
              <div v-else class="avatar-placeholder">
                <svg width="32" height="32" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
                </svg>
              </div>
              <div class="avatar-actions">
                <input
                  ref="fileInput"
                  type="file"
                  accept="image/*"
                  @change="handleAvatarUpload"
                  style="display: none;"
                />
                <BaseButton
                  type="button"
                  size="sm"
                  variant="secondary"
                  @click="$refs.fileInput.click()"
                >
                  选择头像
                </BaseButton>
                <BaseButton
                  v-if="form.avatar"
                  type="button"
                  size="sm"
                  variant="danger"
                  @click="removeAvatar"
                >
                  移除
                </BaseButton>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">设备绑定</label>
            <div class="checkbox-group">
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.bindDevice" />
                <span class="checkbox-mark"></span>
                <span class="checkbox-label">绑定当前设备</span>
              </label>
            </div>
            <p class="form-help">绑定设备后，该身份只能在指定设备上使用</p>
          </div>
          
          <div class="form-group">
            <label class="form-label">生物特征</label>
            <div class="checkbox-group">
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.enableBiometric" />
                <span class="checkbox-mark"></span>
                <span class="checkbox-label">启用生物特征识别</span>
              </label>
            </div>
            <p class="form-help">启用后可以使用指纹、面部识别等方式进行身份验证</p>
          </div>
          
          <div v-if="form.enableBiometric" class="form-group">
            <label class="form-label">生物特征类型</label>
            <div class="checkbox-group">
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.biometricTypes.fingerprint" />
                <span class="checkbox-mark"></span>
                <span class="checkbox-label">指纹识别</span>
              </label>
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.biometricTypes.face" />
                <span class="checkbox-mark"></span>
                <span class="checkbox-label">面部识别</span>
              </label>
              <label class="checkbox-item">
                <input type="checkbox" v-model="form.biometricTypes.voice" />
                <span class="checkbox-mark"></span>
                <span class="checkbox-label">声纹识别</span>
              </label>
            </div>
          </div>
          
          <div class="form-group">
            <label class="form-label">标签</label>
            <div class="tags-input">
              <div class="tags-list">
                <span v-for="(tag, index) in form.tags" :key="index" class="tag">
                  {{ tag }}
                  <button type="button" @click="removeTag(index)" class="tag-remove">
                    ×
                  </button>
                </span>
              </div>
              <input
                v-model="newTag"
                type="text"
                class="tags-input-field"
                placeholder="输入标签后按回车添加"
                @keyup.enter="addTag"
              />
            </div>
          </div>
          
          <div class="form-actions">
            <BaseButton
              type="button"
              variant="secondary"
              @click="$emit('close')"
            >
              取消
            </BaseButton>
            <BaseButton
              type="submit"
              variant="primary"
              :loading="loading"
            >
              创建身份
            </BaseButton>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { identityAPI } from '@/api/modules/identity'
import BaseButton from '@/components/base/Button.vue'

// 响应式数据
const loading = ref(false)
const newTag = ref('')
const form = reactive({
  name: '',
  description: '',
  avatar: '',
  bindDevice: true,
  enableBiometric: false,
  biometricTypes: {
    fingerprint: false,
    face: false,
    voice: false
  },
  tags: []
})

const errors = reactive({
  name: ''
})

// 方法
const handleAvatarUpload = (event: Event) => {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (file) {
    // 模拟文件上传，这里应该调用实际的上传API
    const reader = new FileReader()
    reader.onload = (e) => {
      form.avatar = e.target?.result as string
    }
    reader.readAsDataURL(file)
  }
}

const removeAvatar = () => {
  form.avatar = ''
}

const addTag = () => {
  const tag = newTag.value.trim()
  if (tag && !form.tags.includes(tag)) {
    form.tags.push(tag)
    newTag.value = ''
  }
}

const removeTag = (index: number) => {
  form.tags.splice(index, 1)
}

const handleSubmit = async () => {
  // 验证表单
  if (!form.name.trim()) {
    errors.name = '请输入身份名称'
    return
  }
  
  errors.name = ''
  loading.value = true
  
  try {
    const requestData = {
      name: form.name.trim(),
      description: form.description.trim() || null,
      avatar: form.avatar || null,
      bindDevice: form.bindDevice,
      enableBiometric: form.enableBiometric,
      biometricTypes: form.enableBiometric ? form.biometricTypes : {},
      tags: form.tags
    }
    
    await identityAPI.createIdentity(requestData)
    $emit('created')
  } catch (error) {
    console.error('创建身份失败:', error)
    alert('创建失败，请重试')
  } finally {
    loading.value = false
  }
}

const $emit = defineEmits<{
  close: []
  created: []
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
  max-width: 500px;
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

.avatar-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar-preview {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.avatar-placeholder {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #f3f4f6;
  color: #9ca3af;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-actions {
  display: flex;
  gap: 8px;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
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

.form-help {
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

.tags-input {
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  padding: 8px;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background-color: #e0e7ff;
  color: #3730a3;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tag-remove {
  border: none;
  background: none;
  color: #6366f1;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  padding: 0;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:hover {
    color: #4f46e5;
  }
}

.tags-input-field {
  width: 100%;
  border: none;
  outline: none;
  background: none;
  font-size: 14px;
  padding: 0;
  
  &::placeholder {
    color: #9ca3af;
  }
}

.form-actions {
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
  
  .avatar-upload {
    flex-direction: column;
    align-items: stretch;
  }
  
  .avatar-actions {
    justify-content: center;
  }
  
  .form-actions {
    flex-direction: column-reverse;
  }
}
</style>