import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: {
      name: '游客'
    }
  }),
  actions: {
    logout() {
      this.token = ''
      localStorage.removeItem('token')
    }
  }
})