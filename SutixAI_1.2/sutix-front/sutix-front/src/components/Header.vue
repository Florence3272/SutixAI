<template>
  <div class="main-header">
    <div class="left">
      <h2>📚 中俄术语库管理系统</h2>
    </div>
    <div class="right" v-if="user">
      <span class="username">欢迎，{{ user.username }}</span>
      <el-button type="danger" size="small" @click="logout">退出登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const user = ref({})
const router = useRouter()

// 安全读取 localStorage 并判断登录状态
const initUser = () => {
  const userStr = localStorage.getItem('userInfo')
  // 1. 先判断是否存在且不是 "undefined" 字符串
  if (!userStr || userStr === 'undefined') {
    user.value = {}
    return
  }
  // 2. 安全解析，捕获异常
  try {
    user.value = JSON.parse(userStr)
  } catch (e) {
    console.error('用户信息解析失败', e)
    localStorage.removeItem('userInfo') // 清除坏数据
    user.value = {}
  }
}

// 页面加载时初始化
onMounted(() => {
  initUser()
})

// 退出登录
const logout = () => {
  localStorage.removeItem('userInfo')
  localStorage.removeItem('token') // 同时清 token
  user.value = {}
  ElMessage.success('退出成功')
  router.push('/login')
}

// 判断是否已登录（对外可用）
const isLogin = () => {
  return !!user.value.id && !!localStorage.getItem('token')
}
</script>

<style scoped>
.main-header {
  height: 60px;
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.left h2 {
  margin: 0;
  font-size: 18px;
  color: #333;
}
.right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.username {
  font-size: 14px;
  color: #666;
}
</style>
