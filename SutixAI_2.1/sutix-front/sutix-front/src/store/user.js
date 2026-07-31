import { defineStore } from 'pinia'

/**
 * 当前登录用户状态 — token + 用户信息（含角色）
 */
export const useUserStore = defineStore('user', {
  state: () => {
    let userInfo = {}
    try {
      const raw = localStorage.getItem('userInfo')
      if (raw) userInfo = JSON.parse(raw)
    } catch (e) {
      // ignore
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo
    }
  },
  getters: {
    /** 是否已登录 */
    isLoggedIn: (state) => !!state.token,
    /** 是否为管理员角色（roleId === 1） */
    isAdmin: (state) => state.userInfo?.roleId === 1,
    /** 是否为普通用户（roleId === 2） */
    isUser: (state) => state.userInfo?.roleId === 2
  },
  actions: {
    /** 登录成功后保存 token 和用户信息 */
    login(token, userInfo) {
      this.token = token
      this.userInfo = userInfo || {}
      localStorage.setItem('token', token)
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})
