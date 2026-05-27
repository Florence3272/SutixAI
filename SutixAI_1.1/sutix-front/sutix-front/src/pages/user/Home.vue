<template>
<Header />
  <div class="home">
    <!-- 全屏Banner -->
    <div class="banner" :style="{backgroundImage: `url(${bannerImg})`}">
      <div class="banner-overlay"></div>
      <div class="banner-content">
        <h1>📚 中-俄 专业术语库平台</h1>
        <p>汇集专业术语 · 中俄对照 · 检索学习一体化服务</p>

        <!-- 首页搜索框 -->
        <div class="search-box">
          <el-input
            v-model="searchText"
            placeholder="输入中文/俄文术语检索"
            style="width: 100%; max-width: 520px"
            @keyup.enter="handleSearch"
            clearable
          >
            <template #append>
              <el-button 
                type="primary" 
                @click="handleSearch" 
                :loading="searchLoading"
              >
                全站搜索
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>


    <!-- 平台功能入口 -->
    <div class="section">
      <div class="section-title">
        <span class="num">01</span>
        <span>平台功能导航</span>
      </div>
      <div class="module-grid">
        <div class="module-card" @click="$router.push('/term-list')">
          <div class="card-icon">📖</div>
          <div class="card-title">总术语库管理</div>
          <div class="card-desc">所有分类术语统一管理、批量操作</div>
        </div>
        <div class="module-card" @click="$router.push('/term-study')">
          <div class="card-icon">🎓</div>
          <div class="card-title">综合术语学习</div>
          <div class="card-desc">跨分类卡片翻转记忆，中俄对照背诵</div>
        </div>
        <div class="module-card disabled">
          <div class="card-icon">📄</div>
          <div class="card-title">语料库</div>
          <div class="card-desc">中俄专业语料查询（开发中）</div>
          <el-tag size="small" type="info" class="mt-2">即将上线</el-tag>
        </div>
        <div class="module-card disabled">
          <div class="card-icon">🔄</div>
          <div class="card-title">在线翻译</div>
          <div class="card-desc">术语优先智能翻译（开发中）</div>
          <el-tag size="small" type="info" class="mt-2">即将上线</el-tag>
        </div>
      </div>
    </div>
  </div>

    <!-- 专业分类数据库展示区 -->
    <div class="section category-section">
      <div class="section-title">
        <span class="num">02</span>
        <span>专业分类数据库</span>
      </div>
      
      <div class="category-grid">
        <div
          v-for="(category, index) in categoryList"
          :key="index"
          class="category-card"
          @click="goToCategoryDatabase(category.id)"
          :style="{ borderColor: category.color + '33' }"
        >
          <!-- 图标圆形背景 -->
          <div class="category-icon-wrap" :style="{ background: category.color + '22' }">
            <div class="category-icon" :style="{ color: category.color }">{{ category.icon }}</div>
          </div>

          <div class="category-title">{{ category.name }}</div>
          
          <div class="category-count" :style="{ color: category.color }">
            收录 {{ category.termCount }} 条
          </div>
          
          <div class="category-desc">{{ category.description }}</div>
          
          <el-button type="primary" size="small" class="enter-btn" :style="{ background: category.color, borderColor: category.color }">
            进入数据库
          </el-button>
        </div>
      </div>
    </div>

    <!-- 精选中俄术语卡片区 -->
    <div class="section">
      <div class="section-title">
        <span class="num">03</span>
        <span>精选中俄术语</span>
      </div>
      
      <!-- 空数据状态 -->
      <div v-if="allTerms.length === 0" class="empty-state">
        <el-empty description="暂无术语数据，请先添加术语" />
        <el-button type="primary" @click="$router.push('/term-list')" class="mt-4">
          去添加术语
        </el-button>
      </div>
      
      <!-- 轮播容器 -->
      <div v-else class="carousel-container">
        <el-button
          class="arrow-btn left"
          icon="el-icon-arrow-left"
          circle
          @click="prevTerm"
          :disabled="isAnimating"
        />
        <div class="term-grid" :class="{ 'fade-enter': isAnimating }">
          <div
            v-for="(item, index) in visibleTerms"
            :key="startIndex + index"
            class="term-card"
            @click="goToTermDetail(startIndex + index)"
          >
            <div class="zh">{{ item.name }}</div>
            <div class="ru">{{ item.ruName }}</div>
          </div>
        </div>
        <el-button
          class="arrow-btn right"
          icon="el-icon-arrow-right"
          circle
          @click="nextTerm"
          :disabled="isAnimating"
        />
        
        <!-- 轮播指示器 -->
        <div class="carousel-indicators">
          <span
            v-for="(dot, index) in totalPages"
            :key="index"
            class="indicator-dot"
            :class="{ active: currentPage === index }"
            @click="goToPage(index)"
          ></span>
        </div>
      </div>
    </div>

</template>

<script setup>
import Header from '@/components/Header.vue'
import bannerImg from '@/img/500d20e282f6d5ed4029532465757cd1.jpg'

import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const searchText = ref('')
const allTerms = ref([])
const startIndex = ref(0)
const searchLoading = ref(false)
const isAnimating = ref(false)

// 【升级】每个分类自带专属颜色
const categoryList = ref([
  { id: 'mechanical', name: '机械工程', icon: '⚙️', color: '#409eff', termCount: 1256, description: '机械设计、制造、自动化相关术语' },
  { id: 'electrical', name: '电气工程', icon: '⚡', color: '#e6a23c', termCount: 987, description: '电力系统、电子技术、自动化控制' },
  { id: 'chemical', name: '化学化工', icon: '🧪', color: '#67c23a', termCount: 1542, description: '有机化学、无机化学、化工工艺' },
  { id: 'medical', name: '医药卫生', icon: '💊', color: '#f56c6c', termCount: 2103, description: '临床医学、药学、生物医学' },
  { id: 'computer', name: '计算机科学', icon: '💻', color: '#8e44ad', termCount: 1876, description: '软件开发、人工智能、网络技术' },
  { id: 'construction', name: '建筑工程', icon: '🏗️', color: '#d35400', termCount: 1123, description: '建筑设计、结构工程、施工技术' },
  { id: 'transport', name: '交通运输', icon: '🚄', color: '#2c3e50', termCount: 865, description: '铁路、公路、航空、水运术语' },
  { id: 'energy', name: '能源动力', icon: '🔋', color: '#27ae60', termCount: 742, description: '石油、天然气、新能源技术' },
  { id: 'legal', name: '法律法务', icon: '⚖️', color: '#7f8c8d', termCount: 654, description: '中俄法律、合同、商务法务' },
  { id: 'economic', name: '经济贸易', icon: '💰', color: '#f39c12', termCount: 921, description: '国际贸易、金融、市场营销' },
  { id: 'agricultural', name: '农业科学', icon: '🌾', color: '#2ecc71', termCount: 587, description: '农学、畜牧、林业、渔业' },
  { id: 'environmental', name: '环境科学', icon: '🌍', color: '#3498db', termCount: 432, description: '环境保护、生态工程、污染治理' }
])

// 每页展示4张中俄术语卡片
const visibleTerms = computed(() => {
  const end = startIndex.value + 4
  return allTerms.value.slice(startIndex.value, end)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(allTerms.value.length / 4)
})

// 当前页码
const currentPage = computed(() => {
  return Math.floor(startIndex.value / 4)
})

// 读取本地中俄术语数据
const loadTerms = () => {
  try {
    const data = localStorage.getItem('sutix-term-list')
    if (data) {
      allTerms.value = JSON.parse(data)
    } else {
      const sampleTerms = [
        { name: '人工智能', ruName: 'Искусственный интеллект', category: 'computer' },
        { name: '机器学习', ruName: 'Машинное обучение', category: 'computer' },
        { name: '神经网络', ruName: 'Нейронная сеть', category: 'computer' },
        { name: '深度学习', ruName: 'Глубокое обучение', category: 'computer' },
        { name: '三相电路', ruName: 'Трехфазная цепь', category: 'electrical' },
        { name: '变压器', ruName: 'Трансформатор', category: 'electrical' },
        { name: '化学反应', ruName: 'Химическая реакция', category: 'chemical' },
        { name: '催化剂', ruName: 'Катализатор', category: 'chemical' }
      ]
      allTerms.value = sampleTerms
      localStorage.setItem('sutix-term-list', JSON.stringify(sampleTerms))
    }
  } catch (error) {
    console.error('加载术语数据失败:', error)
    ElMessage.error('加载术语数据失败，请刷新页面重试')
  }
}

// 上一组
const prevTerm = () => {
  if (isAnimating.value) return
  isAnimating.value = true
  startIndex.value = startIndex.value > 0 ? startIndex.value - 4 : Math.max(0, allTerms.value.length - 4)
  setTimeout(() => isAnimating.value = false, 300)
}

// 下一组
const nextTerm = () => {
  if (isAnimating.value) return
  isAnimating.value = true
  startIndex.value = startIndex.value + 4 < allTerms.value.length ? startIndex.value + 4 : 0
  setTimeout(() => isAnimating.value = false, 300)
}

// 跳转到指定页
const goToPage = (pageIndex) => {
  if (isAnimating.value || pageIndex === currentPage.value) return
  isAnimating.value = true
  startIndex.value = pageIndex * 4
  setTimeout(() => isAnimating.value = false, 300)
}

// 术语详情
const goToTermDetail = (index) => {
  router.push(`/term-detail?index=${index}`)
}

// 进入分类数据库
const goToCategoryDatabase = (categoryId) => {
  router.push({ path: `/category/${categoryId}` })
}

// 搜索
const handleSearch = () => {
  if (!searchText.value.trim()) return ElMessage.warning('请输入搜索内容')
  searchLoading.value = true
  setTimeout(() => {
    searchLoading.value = false
    router.push({ path: '/term-list', query: { search: searchText.value.trim() } })
  }, 500)
}

onMounted(() => loadTerms())

watch(() => allTerms.value.length, (newLength) => {
  if (startIndex.value >= newLength && newLength > 0) {
    startIndex.value = Math.max(0, newLength - 4)
  }
})
</script>

<style scoped>
.home {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
}

/* Banner */
.banner {
  position: relative;
  width: 100%;
  height: 400px;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
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
  padding: 120px 20px 0;
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

/* 区块 */
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

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

/* 轮播 */
.carousel-container {
  position: relative;
  padding: 0 40px 40px;
}
.arrow-btn {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 10;
}
.arrow-btn.left { left: 0; }
.arrow-btn.right { right: 0; }
.carousel-indicators {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}
.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #dcdfe6;
  cursor: pointer;
  transition: all 0.3s;
}
.indicator-dot.active {
  width: 24px;
  border-radius: 4px;
  background: #409eff;
}

/* 术语卡片 */
.term-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  transition: opacity 0.3s ease;
}
.term-grid.fade-enter { opacity: 0; }
.term-card {
  background: #fff;
  border: 1px solid #eee;
  padding: 32px 16px;
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
.zh { font-size: 22px; font-weight: bold; color: #333; margin-bottom: 12px; }
.ru { font-size: 16px; color: #666; line-height: 1.4; }

/* ====================================== */
/* 【高颜值升级】专业分类卡片样式 */
/* ====================================== */
.category-section {
  
  padding: 50px 35px;
  border-radius: 16px;
  box-shadow: 0 3px 20px rgba(0,0,0,0.06);
}
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}
.category-card {
  position: relative;
  border-width: 2px;
  border-style: solid;
  border-color: #eee;
  border-radius: 16px;
  padding: 30px 24px;
  text-align: center;
  transition: all 0.35s ease;
  cursor: pointer;
  background: #fff;
}
.category-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0,0,0,0.08);
}

/* 图标容器 */
.category-icon-wrap {
  width: 70px;
  height: 70px;
  margin: 0 auto 18px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.category-icon {
  font-size: 36px;
}

/* 文字 */
.category-title {
  font-size: 19px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}
.category-count {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 12px;
}
.category-desc {
  font-size: 13px;
  color: #7f8c8d;
  line-height: 1.5;
  margin-bottom: 20px;
  min-height: 38px;
}

/* 按钮 */
.enter-btn {
  border-radius: 8px;
  height: 34px;
  font-size: 13px;
  font-weight: 500;
}

/* 功能模块 */
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
  box-shadow: 0 8px 24px rgba(64,158,255,0.15);
}
.module-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.card-icon { font-size: 36px; margin-bottom: 15px; }
.card-title { font-size: 18px; font-weight: bold; margin-bottom: 10px; }
.card-desc { color: #666; font-size: 14px; line-height: 1.6; }

/* 响应式 */
@media (max-width: 1200px) {
  .category-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 1024px) {
  .term-grid { grid-template-columns: repeat(2, 1fr); }
  .category-grid { grid-template-columns: repeat(2, 1fr); }
  .module-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .banner { height: 350px; }
  .banner-content { padding-top: 100px; }
  .banner-content h1 { font-size: 28px; }
  .term-grid, .category-grid, .module-grid { grid-template-columns: 1fr; }
}
</style>
