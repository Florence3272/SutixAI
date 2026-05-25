<template>
  <div class="home">
    <!-- 全屏Banner -->
    <div class="banner">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>📚 中-英-俄 专业术语库平台</h1>
        <p>汇集专业术语 · 三语对照 · 检索学习一体化服务</p>

        <!-- 首页搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchText"
            placeholder="输入中文/英文/俄文术语检索"
            style="width: 520px"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">全站搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>

    <!-- 精选三语术语卡片区 -->
    <div class="section">
      <div class="section-title">
        <span class="num">01</span>
        <span>精选三语术语</span>
      </div>
      <div class="carousel-container">
        <el-button
          class="arrow-btn left"
          icon="el-icon-arrow-left"
          circle
          @click="prevTerm"
        />
        <div class="term-grid">
          <div
            v-for="(item, index) in visibleTerms"
            :key="index"
            class="term-card"
            @click="$router.push('/term-detail?index=' + (startIndex + index))"
          >
            <div class="zh">{{ item.name }}</div>
            <div class="en">{{ item.enName }}</div>
            <div class="ru">{{ item.ruName }}</div>
          </div>
        </div>
        <el-button
          class="arrow-btn right"
          icon="el-icon-arrow-right"
          circle
          @click="nextTerm"
        />
      </div>
    </div>

    <!-- 平台功能入口 -->
    <div class="section">
      <div class="section-title">
        <span class="num">02</span>
        <span>平台功能导航</span>
      </div>
      <div class="module-grid">
        <div class="module-card" @click="$router.push('/term-list')">
          <div class="card-icon">📖</div>
          <div class="card-title">术语库管理</div>
          <div class="card-desc">三语术语新增、编辑、删除、分类检索</div>
        </div>
        <div class="module-card" @click="$router.push('/term-study')">
          <div class="card-icon">🎓</div>
          <div class="card-title">术语学习</div>
          <div class="card-desc">卡片翻转记忆，中英俄对照背诵</div>
        </div>
        <div class="module-card disabled">
          <div class="card-icon">📄</div>
          <div class="card-title">语料库</div>
          <div class="card-desc">双语专业语料查询（开发中）</div>
        </div>
        <div class="module-card disabled">
          <div class="card-icon">🔄</div>
          <div class="card-title">在线翻译</div>
          <div class="card-desc">术语优先智能翻译（开发中）</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchText = ref('')
const allTerms = ref([])
const startIndex = ref(0)

// 每页展示4张三语术语卡片
const visibleTerms = computed(() => {
  const end = startIndex.value + 4
  return allTerms.value.slice(startIndex.value, end)
})

// 读取本地三语术语数据
const loadTerms = () => {
  const data = localStorage.getItem('sutix-term-list')
  if (data) {
    allTerms.value = JSON.parse(data)
  }
}

// 上一组
const prevTerm = () => {
  if (startIndex.value > 0) {
    startIndex.value -= 4
  } else {
    startIndex.value = Math.max(0, allTerms.value.length - 4)
  }
}

// 下一组
const nextTerm = () => {
  if (startIndex.value + 4 < allTerms.value.length) {
    startIndex.value += 4
  } else {
    startIndex.value = 0
  }
}

// 首页搜索跳转
const handleSearch = () => {
  if (!searchText.value) return
  router.push({
    path: '/term-list',
    query: { search: searchText.value }
  })
}

onMounted(() => {
  loadTerms()
})
</script>

<style scoped>
.home {
  width: 100%;
}
/* 顶部大图Banner */
.banner {
  position: relative;
  width: 100%;
  height: 400px;
  background: url('https://picsum.photos/1920/400?random=10') center/cover no-repeat;
}
.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.55);
}
.banner-content {
  position: relative;
  z-index: 1;
  text-align: center;
  padding-top: 120px;
  color: #fff;
}
.banner-content h1 {
  font-size: 40px;
  margin-bottom: 15px;
  letter-spacing: 4px;
}
.banner-content p {
  font-size: 18px;
  opacity: 0.9;
  margin-bottom: 30px;
  letter-spacing: 2px;
}
/* 通用区块样式 */
.section {
  max-width: 1200px;
  margin: 60px auto;
  padding: 0 20px;
}
.section-title {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #333;
}
.section-title .num {
  background: #409eff;
  color: #fff;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  margin-right: 12px;
}
/* 术语卡片轮播容器 */
.carousel-container {
  position: relative;
  padding: 0 40px;
}
.arrow-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
}
.arrow-btn.left {
  left: 0;
}
.arrow-btn.right {
  right: 0;
}
/* 三语术语卡片网格 */
.term-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
.term-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 26px 16px;
  text-align: center;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.term-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 8px 24px rgba(64,158,255,0.15);
  border-color: #409eff;
}
/* 三语文字样式 */
.zh {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}
.en {
  font-size: 15px;
  color: #409eff;
  margin-bottom: 4px;
}
.ru {
  font-size: 14px;
  color: #666;
}
/* 功能模块网格 */
.module-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}
.module-card {
  background: #fff;
  padding: 30px 20px;
  text-align: center;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  cursor: pointer;
  transition: all 0.3s;
}
.module-card:hover:not(.disabled) {
  transform: translateY(-5px);
}
.module-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.card-icon {
  font-size: 36px;
  margin-bottom: 15px;
}
.card-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}
.card-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}
</style>