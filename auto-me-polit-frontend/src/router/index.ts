import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import { useAppStore } from '@/store/app'
import { useAuthStore } from '@/store/auth'
import LoadingOverlay from '@/components/common/LoadingOverlay.vue'

// 静态导入页面组件（更好的代码分割）
const Dashboard = () => import('@/views/dashboard/index.vue')
const IdentityManagement = () => import('@/views/identity/index.vue')
const AccountManagement = () => import('@/views/account/index.vue')
const PolicyManagement = () => import('@/views/policy/index.vue')
const AuthLogin = () => import('@/views/auth/Login.vue')
const AuthRegister = () => import('@/views/auth/Register.vue')
const AuthSetup = () => import('@/views/auth/Setup.vue')
const Profile = () => import('@/views/Profile.vue')
const Settings = () => import('@/views/Settings.vue')

// 定义路由配置
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: {
      title: '仪表盘',
      requiresAuth: true,
      icon: 'HomeIcon',
      description: '统一身份认证驾驶舱仪表盘'
    }
  },
  {
    path: '/identity',
    name: 'Identity',
    component: IdentityManagement,
    meta: {
      title: '身份管理',
      requiresAuth: true,
      icon: 'UserIcon',
      description: '管理您的统一身份标识'
    }
  },
  {
    path: '/account',
    name: 'Account',
    component: AccountManagement,
    meta: {
      title: '账户关联',
      requiresAuth: true,
      icon: 'LinkIcon',
      description: '管理第三方平台账户关联'
    }
  },
  {
    path: '/policy',
    name: 'Policy',
    component: PolicyManagement,
    meta: {
      title: '共享策略',
      requiresAuth: true,
      icon: 'ShieldCheckIcon',
      description: '配置身份属性共享策略'
    }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: Profile,
    meta: {
      title: '个人资料',
      requiresAuth: true,
      icon: 'UserCircleIcon',
      description: '查看和编辑个人资料'
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: {
      title: '系统设置',
      requiresAuth: true,
      icon: 'CogIcon',
      description: '系统配置和个人偏好设置'
    }
  },
  // 认证相关路由（不需要登录）
  {
    path: '/auth/login',
    name: 'Login',
    component: AuthLogin,
    meta: {
      title: '用户登录',
      requiresAuth: false,
      hideForAuth: true,
      description: '登录到统一身份认证平台'
    }
  },
  {
    path: '/auth/register',
    name: 'Register',
    component: AuthRegister,
    meta: {
      title: '用户注册',
      requiresAuth: false,
      hideForAuth: true,
      description: '创建新的统一身份账户'
    }
  },
  {
    path: '/auth/setup',
    name: 'Setup',
    component: AuthSetup,
    meta: {
      title: '身份设置',
      requiresAuth: false,
      hideForAuth: true,
      description: '完成身份验证设置'
    }
  },
  // 错误页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面未找到',
      requiresAuth: false
    }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else if (to.hash) {
      return { el: to.hash }
    } else {
      return { top: 0 }
    }
  }
})

// 全局导航守卫
router.beforeEach(async (to, from, next) => {
  const appStore = useAppStore()
  const authStore = useAuthStore()

  try {
    // 显示加载指示器
    appStore.setLoading(true)

    // 检查是否需要认证
    if (to.meta.requiresAuth) {
      // 如果未登录，跳转到登录页
      if (!authStore.isAuthenticated) {
        next({
          name: 'Login',
          query: { redirect: to.fullPath }
        })
        return
      }

      // 验证token是否有效
      if (!authStore.isTokenValid) {
        await authStore.refreshToken()
        if (!authStore.isAuthenticated) {
          next({
            name: 'Login',
            query: { redirect: to.fullPath }
          })
          return
        }
      }
    }

    // 如果已登录，隐藏认证相关页面
    if (to.meta.hideForAuth && authStore.isAuthenticated) {
      next('/dashboard')
      return
    }

    // 设置页面标题
    if (to.meta.title) {
      document.title = `${to.meta.title} - 统一身份认证驾驶舱`
    }

    next()
  } catch (error) {
    console.error('导航错误:', error)
    next({
      name: 'Login',
      query: { redirect: to.fullPath }
    })
  } finally {
    appStore.setLoading(false)
  }
})

// 导航完成后
router.afterEach((to, from) => {
  const appStore = useAppStore()
  
  // 记录页面访问日志
  console.log(`页面访问: ${from.name || 'N/A'} -> ${to.name}`)
  
  // 更新当前页面信息
  appStore.setCurrentPage({
    name: to.name as string,
    path: to.path,
    meta: to.meta
  })
  
  // 清除加载状态
  appStore.setLoading(false)
})

// 导航错误处理
router.onError((error) => {
  console.error('路由错误:', error)
  
  // 可以在这里集成错误监控服务
  // Sentry.captureException(error)
})

export default router