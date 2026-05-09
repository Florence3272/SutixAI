<template>
  <div class="login-page">
    <div class="login-box">
      <h2>术语 AI 平台登录</h2>
      <el-input v-model="form.username" placeholder="用户名" class="input-item" />
      <el-input v-model="form.password" placeholder="密码" type="password" class="input-item" />
      <el-button type="primary" @click="login" class="login-btn">登录</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})

const login = async () => {
  const res = await request.post('/user/login', form.value)
  localStorage.setItem('token', res.data.data.token)
  router.push('/')
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}
.login-box {
  width: 400px;
  padding: 30px;
  background: white;
  border-radius: 8px;
}
.input-item {
  margin-bottom: 15px;
}
.login-btn {
  width: 100%;
}
</style>