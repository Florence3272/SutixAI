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

const router = useRouter()
const user = ref(null)

onMounted(() => {
  const u = localStorage.getItem('userInfo')
  if (u) user.value = JSON.parse(u)
})

// 退出登录
const logout = () => {
  localStorage.removeItem('userInfo')
  ElMessage.success('退出成功')
  router.push('/login')
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
