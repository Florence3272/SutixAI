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
        // 修复点1：子路由不能以/开头
        path: 'category/:categoryId', 
        name: 'CategoryDatabase',
        // 修复点2：添加懒加载导入（与其他路由保持一致）
        component: () => import('@/pages/user/CategoryDatabase.vue')
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

export default router
