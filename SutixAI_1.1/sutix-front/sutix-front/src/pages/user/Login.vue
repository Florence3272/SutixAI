<template>
  <div class="login-page">
    <div class="login-card">
      <h2 class="title">{{ isLogin ? '登录' : '注册' }}</h2>

      <el-form v-model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
        <!-- 用户名 -->
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            clearable
          />
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 注册时显示确认密码 -->
        <el-form-item prop="confirmPwd" v-if="!isLogin">
          <el-input
            v-model="form.confirmPwd"
            placeholder="请确认密码"
            prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <!-- 按钮 -->
        <el-button
          type="primary"
          class="submit-btn"
          @click="submit"
        >
          {{ isLogin ? '登录' : '注册' }}
        </el-button>

        <!-- 切换 -->
        <div class="toggle-text" @click="isLogin = !isLogin">
          {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const isLogin = ref(true)
const formRef = ref()

const form = reactive({
  username: '',
  password: '',
  confirmPwd: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPwd: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value) => {
        return value === form.password
      },
      message: '两次密码不一致',
      trigger: 'blur'
    }
  ]
}

// 提交
const submit = async () => {
  await formRef.value.validate()

  if (isLogin.value) {
    doLogin()
  } else {
    doRegister()
  }
}

// 登录
const doLogin = () => {
  const users = JSON.parse(localStorage.getItem('users') || '[]')
  const find = users.find(u => u.username === form.username && u.password === form.password)

  if (!find) {
    ElMessage.error('用户名或密码错误')
    return
  }

  localStorage.setItem('userInfo', JSON.stringify(find))
  ElMessage.success('登录成功')
  router.push('/home')
}

// 注册
const doRegister = () => {
  const users = JSON.parse(localStorage.getItem('users') || '[]')

  const exist = users.some(u => u.username === form.username)
  if (exist) {
    ElMessage.error('用户名已存在')
    return
  }

  users.push({
    username: form.username,
    password: form.password
  })

  localStorage.setItem('users', JSON.stringify(users))
  ElMessage.success('注册成功，请登录')
  isLogin.value = true
}
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 40px 32px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}

.title {
  text-align: center;
  margin-bottom: 30px;
  font-size: 24px;
  color: #333;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.submit-btn {
  height: 44px;
  font-size: 16px;
  margin-top: 8px;
}

.toggle-text {
  text-align: center;
  color: #409eff;
  cursor: pointer;
  font-size: 14px;
}
</style>
