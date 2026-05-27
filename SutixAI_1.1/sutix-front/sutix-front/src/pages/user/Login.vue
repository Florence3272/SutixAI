<template>
  <div class="login-page" @mousemove="handleMouseMove">
    <!-- 动态俄语中文背景 -->
    <div class="word-bg">
      <div
        class="word-item"
        v-for="(item, index) in wordList"
        :key="index"
        :style="getItemStyle(item, index)"
      >
        <span class="ru">{{ item.ru }}</span>
        <span class="cn">{{ item.cn }}</span>
      </div>
    </div>

    <!-- 卷轴书本 外壳 -->
    <div class="book-scroll-wrapper">
      <div class="book-left"></div>
      <div class="login-card">
        <div class="title-group">
          <h2 class="title-chinese">{{ isLogin ? '登录' : '注册' }}</h2>
          <p class="title-russian">{{ isLogin ? 'Вход' : 'Регистрация' }}</p>
        </div>

        <el-form v-model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-form-item prop="confirmPwd" v-if="!isLogin">
            <el-input
              v-model="form.confirmPwd"
              placeholder="请确认密码"
              prefix-icon="Lock"
              show-password
              clearable
            />
          </el-form-item>

          <el-button type="primary" class="submit-btn" @click="submit">
            {{ isLogin ? '登录' : '注册' }}
          </el-button>

          <div class="toggle-text" @click="isLogin = !isLogin">
            {{ isLogin ? '没有账号？去注册' : '已有账号？去登录' }}
          </div>
        </el-form>
      </div>
      <div class="book-right"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const isLogin = ref(true)
const formRef = ref()
// 鼠标坐标
const mousePos = ref({ x: 0, y: 0 })
// 原始文字位置
const originWordList = ref([])
const wordList = ref([])

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

const submit = async () => {
  await formRef.value.validate()
  isLogin.value ? doLogin() : doRegister()
}

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

const doRegister = () => {
  const users = JSON.parse(localStorage.getItem('users') || '[]')
  const exist = users.some(u => u.username === form.username)
  if (exist) {
    ElMessage.error('用户名已存在')
    return
  }
  users.push({ username: form.username, password: form.password })
  localStorage.setItem('users', JSON.stringify(users))
  ElMessage.success('注册成功，请登录')
  isLogin.value = true
}

// 海量俄汉对照词汇
const wordData = [
  { ru: 'Привет', cn: '你好' },{ ru: 'Спасибо', cn: '谢谢' },{ ru: 'Пожалуйста', cn: '不客气' },
  { ru: 'Да', cn: '是' },{ ru: 'Нет', cn: '否' },{ ru: 'Доброе утро', cn: '早上好' },
  { ru: 'Добрый день', cn: '下午好' },{ ru: 'Добрый вечер', cn: '晚上好' },{ ru: 'Как дела?', cn: '你好吗' },
  { ru: 'Хорошо', cn: '很好' },{ ru: 'Пока', cn: '再见' },{ ru: 'Извините', cn: '对不起' },
  { ru: 'Понял', cn: '明白了' },{ ru: 'Помогите', cn: '帮助' },{ ru: 'Вход', cn: '登录' },
  { ru: 'Регистрация', cn: '注册' },{ ru: 'Пользователь', cn: '用户' },{ ru: 'Пароль', cn: '密码' },
  { ru: 'Главная', cn: '首页' },{ ru: 'Язык', cn: '语言' },{ ru: 'Время', cn: '时间' },
  { ru: 'Дом', cn: '家' },{ ru: 'Город', cn: '城市' },{ ru: 'Солнце', cn: '太阳' },
  { ru: 'Небо', cn: '天空' },{ ru: 'Вода', cn: '水' },{ ru: 'Еда', cn: '食物' },
  { ru: 'Книга', cn: '书籍' },{ ru: 'Телефон', cn: '手机' },{ ru: 'Компьютер', cn: '电脑' },
  { ru: 'Друг', cn: '朋友' },{ ru: 'Семья', cn: '家庭' },{ ru: 'Работа', cn: '工作' },
  { ru: 'Учёба', cn: '学习' },{ ru: 'Знание', cn: '知识' },{ ru: 'Мечта', cn: '梦想' },
  { ru: 'Любовь', cn: '爱意' },{ ru: 'Счастье', cn: '幸福' },{ ru: 'Надежда', cn: '希望' },
  { ru: 'Успех', cn: '成功' },{ ru: 'Вперёд', cn: '向前' },{ ru: 'Спокойно', cn: '冷静' },
  { ru: 'Весело', cn: '开心' },{ ru: 'Грустно', cn: '难过' },{ ru: 'Сегодня', cn: '今天' },
  { ru: 'Завтра', cn: '明天' },{ ru: 'Вчера', cn: '昨天' },{ ru: 'Число', cn: '数字' },
  { ru: 'Цвет', cn: '颜色' },{ ru: 'Музыка', cn: '音乐' },{ ru: 'Фильм', cn: '电影' },
  { ru: 'Спорт', cn: '运动' },{ ru: 'Путешествие', cn: '旅行' },{ ru: 'Красиво', cn: '美丽' },
  { ru: 'Сильно', cn: '强大' },{ ru: 'Мудро', cn: '聪慧' },{ ru: 'Честно', cn: '真诚' },
  { ru: 'Терпение', cn: '耐心' },{ ru: 'Сила', cn: '力量' },{ ru: 'Душа', cn: '心灵' },
  { ru: 'Мысли', cn: '思绪' },{ ru: 'Звук', cn: '声音' },{ ru: 'Запах', cn: '气味' },
  { ru: 'Свет', cn: '光芒' },{ ru: 'Тень', cn: '阴影' }
]

// 鼠标移动监听
const handleMouseMove = (e) => {
  mousePos.value.x = e.clientX
  mousePos.value.y = e.clientY
}

// 计算文字偏移避让
const getItemStyle = (item, index) => {
  let offsetX = 0, offsetY = 0
  const pageW = document.documentElement.clientWidth
  const pageH = document.documentElement.clientHeight
  const itemX = item.x / 100 * pageW
  const itemY = item.y / 100 * pageH

  // 计算距离
  const disX = mousePos.value.x - itemX
  const disY = mousePos.value.y - itemY
  const distance = Math.sqrt(disX * disX + disY * disY)
  const range = 120

  // 范围内驱离
  if (distance < range && distance > 0) {
    const force = (range - distance) / range * 35
    offsetX = -(disX / distance) * force
    offsetY = -(disY / distance) * force
  }

  return {
    left: `calc(${item.x}% + ${offsetX}px)`,
    top: `calc(${item.y}% + ${offsetY}px)`,
    animationDelay: `${index * 0.15}s`,
    fontSize: `${item.size}px`,
    opacity: item.opacity
  }
}

onMounted(() => {
  originWordList.value = wordData.map(item => ({
    ...item,
    x: Math.random() * 90,
    y: Math.random() * 90,
    size: 11 + Math.random() * 9,
    opacity: (0.25 + Math.random() * 0.45).toFixed(2)
  }))
  wordList.value = [...originWordList.value]
})
</script>

<style scoped>
.login-page {
  width: 100vw;
  height: 100vh;
  background: linear-gradient(135deg, #f7f8fa 0%, #eef1f5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.word-bg {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.word-item {
  position: absolute;
  color: #909399;
  white-space: nowrap;
  animation: wordMove 9s linear infinite;
  transition: left 0.2s ease, top 0.2s ease;
}

.word-item .ru {
  font-weight: 500;
  margin-right: 5px;
  color: #6e7481;
}
.word-item .cn {
  color: #909399;
}

@keyframes wordMove {
  0% { transform: translateY(0) translateX(0); }
  50% { transform: translateY(-20px) translateX(12px); }
  100% { transform: translateY(0) translateX(0); }
}

.book-scroll-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  filter: drop-shadow(0 8px 24px rgba(0, 0, 0, 0.08));
  z-index: 10;
}

.book-left {
  width: 22px;
  height: 380px;
  background: #e8e9ed;
  border-radius: 4px 0 0 4px;
  box-shadow: inset -4px 0 8px rgba(0, 0, 0, 0.05);
}
.book-right {
  width: 22px;
  height: 380px;
  background: #e8e9ed;
  border-radius: 0 4px 4px 0;
  box-shadow: inset 4px 0 8px rgba(0, 0, 0, 0.05);
}

.login-card {
  width: 420px;
  background: #ffffff;
  padding: 50px 36px;
  border: 1px solid #e8e9ed;
  border-radius: 2px;
}

.title-group {
  text-align: center;
  margin-bottom: 32px;
}
.title-chinese {
  font-size: 26px;
  color: #333333;
  font-weight: 600;
  margin: 0;
  letter-spacing: 4px;
}
.title-russian {
  font-size: 14px;
  color: #909399;
  margin: 4px 0 0;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

:deep(.el-input__wrapper) {
  background: #fafbfc !important;
  border-radius: 8px;
  border: 1px solid #e8e9ed !important;
  box-shadow: none !important;
}
:deep(.el-input__wrapper:hover) {
  border-color: #dcdfe6 !important;
}
:deep(.el-input__wrapper.is-focus) {
  border-color: #409eff !important;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1) !important;
}

.submit-btn {
  height: 46px;
  font-size: 16px;
  background: #409eff;
  border-color: #409eff;
  border-radius: 8px;
  font-weight: 500;
  letter-spacing: 2px;
}
.submit-btn:hover {
  background: #3388ff !important;
  border-color: #3388ff !important;
}

.toggle-text {
  text-align: center;
  color: #909399;
  cursor: pointer;
  font-size: 14px;
}
</style>
