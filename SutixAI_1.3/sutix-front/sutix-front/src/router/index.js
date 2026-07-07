import { createRouter, createWebHistory } from 'vue-router'
import UserLayout from '@/layouts/UserLayout.vue'

const routes = [
  {
    path: '/',
    component: UserLayout,
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/pages/user/Home.vue')
      },
      {
        path: 'login',
        name: 'Login',
        component: () => import('@/pages/user/Login.vue')
      },
      {
        // 修复点1：子路由不能以/开头
        path: 'category/:categoryId', 
        name: 'CategoryDatabase',
        // 修复点2：添加懒加载导入（与其他路由保持一致）
        component: () => import('@/pages/user/CategoryDatabase.vue')
      },
      {
        path: 'term-list',
        name: 'TermList',
        component: () => import('@/pages/user/TermList.vue')
      },
      {
        path: 'term-detail',
        name: 'TermDetail',
        component: () => import('@/pages/user/TermDetail.vue')
      },
      {
        path: 'term-study',
        name: 'TermStudy',
        component: () => import('@/pages/user/TermStudy.vue')
      },
      {
        path: '/corpus-compare',
        name: 'CorpusCompare',
        component: () => import('@/pages/user/CorpusCompare.vue'),
        meta: { title: '中俄双语语料库' }
      },
      {
        path: 'translator',
        name: 'Translator',
        component: () => import('@/pages/user/Translator.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 登录拦截：未登录只能进 login
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('userInfo')
  // 如果去登录页 → 放行
  if (to.path === '/login') {
    next()
  }
  // 没登录 → 强制去登录
  else if (!user) {
    next('/login')
  }
  // 已登录 → 正常访问
  else {
    next()
  }
})

export default router
