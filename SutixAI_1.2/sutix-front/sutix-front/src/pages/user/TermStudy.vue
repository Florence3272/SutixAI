<template>
  <div class="study-wrap">
    <div class="title">📖 术语学习记忆</div>

    <!-- 卡片区域 -->
    <div class="card-box" @click="flipCard">
      <div class="term-card" :class="{ flipped : isFlipped }">
        <!-- 正面：中文术语 -->
        <div class="card-front">
          <div class="word">{{ currentTerm.name }}</div>
          <div class="tip">点击卡片查看释义</div>
        </div>
        <!-- 背面：英+俄+解释 -->
        <div class="card-back">
          <div class="line"><strong>英文：</strong>{{ currentTerm.enName || '无' }}</div>
          <div class="line"><strong>俄文：</strong>{{ currentTerm.ruName || '无' }}</div>
          <div class="line"><strong>解释：</strong>{{ currentTerm.desc || '暂无' }}</div>
        </div>
      </div>
    </div>

    <!-- 按钮 -->
    <div class="btn-group">
      <el-button @click="prevTerm" size="large">上一张</el-button>
      <el-button type="primary" @click="randomTerm" size="large">随机换题</el-button>
      <el-button @click="nextTerm" size="large">下一张</el-button>
    </div>

    <div class="count-info">
      当前第 {{ nowIndex + 1 }} / 共 {{ termList.length }} 条术语
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const termList = ref([])
const nowIndex = ref(0)
const isFlipped = ref(false)

// 读取本地术语库
const getLocalTerm = () => {
  const data = localStorage.getItem('sutix-term-list')
  return data ? JSON.parse(data) : []
}

// 当前学习的术语
const currentTerm = ref({})

// 初始化
const initStudy = () => {
  termList.value = getLocalTerm()
  if (termList.value.length > 0) {
    currentTerm.value = termList.value[0]
    nowIndex.value = 0
  }
}

// 翻转卡片
const flipCard = () => {
  isFlipped.value = !isFlipped.value
}

// 上一张
const prevTerm = () => {
  isFlipped.value = false
  if (nowIndex.value <= 0) {
    nowIndex.value = termList.value.length - 1
  } else {
    nowIndex.value--
  }
  currentTerm.value = termList.value[nowIndex.value]
}

// 下一张
const nextTerm = () => {
  isFlipped.value = false
  if (nowIndex.value >= termList.value.length - 1) {
    nowIndex.value = 0
  } else {
    nowIndex.value++
  }
  currentTerm.value = termList.value[nowIndex.value]
}

// 随机术语
const randomTerm = () => {
  isFlipped.value = false
  const max = termList.value.length
  let rnd = Math.floor(Math.random() * max)
  // 避免和当前一样
  while(rnd === nowIndex.value && max > 1){
    rnd = Math.floor(Math.random() * max)
  }
  nowIndex.value = rnd
  currentTerm.value = termList.value[rnd]
}

onMounted(() => {
  initStudy()
})
</script>

<style scoped>
.study-wrap {
  padding: 40px 20px;
  text-align: center;
}
.title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 40px;
  color: #333;
}
.card-box {
  width: 500px;
  height: 300px;
  margin: 0 auto 40px;
  perspective: 1000px;
  cursor: pointer;
}
.term-card {
  width: 100%;
  height: 100%;
  position: relative;
  transform-style: preserve-3d;
  transition: transform 0.6s ease;
}
.term-card.flipped {
  transform: rotateY(180deg);
}
.card-front,
.card-back {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  backface-visibility: hidden;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  padding: 30px;
}
.card-front {
  background: #409eff;
  color: #fff;
}
.card-back {
  background: #fff;
  color: #333;
  transform: rotateY(180deg);
  border: 1px solid #eee;
}
.word {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 20px;
}
.tip {
  font-size: 14px;
  opacity: 0.8;
}
.line {
  font-size: 16px;
  margin: 8px 0;
  text-align: left;
  width: 100%;
}
.btn-group {
  margin-bottom: 20px;
}
.count-info {
  font-size: 15px;
  color: #666;
}
</style>