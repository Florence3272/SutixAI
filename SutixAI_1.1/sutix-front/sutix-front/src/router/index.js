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
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局守卫：无条件放行所有页面
router.beforeEach((to, from, next) => {
  next() // 不校验、不跳转、直接过
})

export default router