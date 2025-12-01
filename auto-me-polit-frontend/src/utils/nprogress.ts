import NProgress from 'nprogress'
import type { Router } from 'vue-router'

/**
 * 配置NProgress进度条
 */
export function setupNProgress(router: Router) {
  // 配置NProgress
  NProgress.configure({
    showSpinner: false,
    minimum: 0.2,
    speed: 500,
    trickleSpeed: 200,
    easing: 'ease',
    parent: 'body'
  })

  // 路由开始时显示进度条
  router.beforeEach(() => {
    NProgress.start()
  })

  // 路由完成时隐藏进度条
  router.afterEach(() => {
    NProgress.done()
  })
}