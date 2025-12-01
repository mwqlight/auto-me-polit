<template>
  <button
    :class="buttonClasses"
    :disabled="disabled || loading"
    @click="handleClick"
  >
    <span v-if="loading" class="loading-spinner"></span>
    <slot v-if="!loading"></slot>
    <span v-else>{{ loadingText || '加载中...' }}</span>
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface Props {
  variant?: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info'
  size?: 'sm' | 'md' | 'lg'
  disabled?: boolean
  loading?: boolean
  loadingText?: string
  block?: boolean
  round?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'primary',
  size: 'md',
  disabled: false,
  loading: false,
  block: false,
  round: false
})

const emit = defineEmits<{
  (e: 'click', event: MouseEvent): void
}>()

const buttonClasses = computed(() => {
  return [
    'base-button',
    `base-button--${props.variant}`,
    `base-button--${props.size}`,
    {
      'base-button--disabled': props.disabled,
      'base-button--loading': props.loading,
      'base-button--block': props.block,
      'base-button--round': props.round
    }
  ]
})

const handleClick = (event: MouseEvent) => {
  if (!props.disabled && !props.loading) {
    emit('click', event)
  }
}
</script>

<style lang="scss" scoped>
.base-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border: 1px solid transparent;
  border-radius: 6px;
  font-weight: 500;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
  position: relative;
  overflow: hidden;
  
  &:disabled,
  &--disabled {
    opacity: 0.6;
    cursor: not-allowed;
    pointer-events: none;
  }
  
  &--loading {
    pointer-events: none;
  }
  
  &--block {
    width: 100%;
  }
  
  &--round {
    border-radius: 50px;
  }
  
  // 尺寸变体
  &--sm {
    padding: 4px 12px;
    font-size: 12px;
    height: 32px;
  }
  
  &--md {
    padding: 8px 16px;
    font-size: 14px;
    height: 40px;
  }
  
  &--lg {
    padding: 12px 24px;
    font-size: 16px;
    height: 48px;
  }
  
  // 颜色变体
  &--primary {
    background-color: #3b82f6;
    color: white;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #2563eb;
    }
    
    &:active:not(:disabled):not(.base-button--loading) {
      background-color: #1d4ed8;
    }
  }
  
  &--secondary {
    background-color: #f9fafb;
    color: #1f2937;
    border-color: #e5e7eb;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #f3f4f6;
    }
  }
  
  &--success {
    background-color: #10b981;
    color: white;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #059669;
    }
    
    &:active:not(:disabled):not(.base-button--loading) {
      background-color: #047857;
    }
  }
  
  &--danger {
    background-color: #ef4444;
    color: white;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #dc2626;
    }
    
    &:active:not(:disabled):not(.base-button--loading) {
      background-color: #b91c1c;
    }
  }
  
  &--warning {
    background-color: #f59e0b;
    color: white;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #d97706;
    }
    
    &:active:not(:disabled):not(.base-button--loading) {
      background-color: #b45309;
    }
  }
  
  &--info {
    background-color: #06b6d4;
    color: white;
    
    &:hover:not(:disabled):not(.base-button--loading) {
      background-color: #0891b2;
    }
    
    &:active:not(:disabled):not(.base-button--loading) {
      background-color: #0e7490;
    }
  }
  
  // 加载动画
  .loading-spinner {
    display: inline-block;
    width: 14px;
    height: 14px;
    border: 2px solid transparent;
    border-top: 2px solid currentColor;
    border-radius: 50%;
    animation: spin 1s linear infinite;
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>