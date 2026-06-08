<template>
  <div class="app-container">
    <!-- 顶部全局导航栏 -->
    <header class="top-header">
      <div class="logo">📚 综合术语学习平台</div>
      <div class="main-nav">
        <div 
          class="nav-item" 
          :class="{ active: mainModule === 'term' }"
          @click="switchMainModule('term')"
        >
          术语记忆
        </div>
        <div 
          class="nav-item" 
          :class="{ active: mainModule === 'video' }"
          @click="switchMainModule('video')"
        >
          视频学习
        </div>
        <div 
          class="nav-item" 
          :class="{ active: mainModule === 'wrong' }"
          @click="switchMainModule('wrong')"
        >
          错题回顾
        </div>
        <div 
          class="nav-item" 
          :class="{ active: mainModule === 'practice' }"
          @click="switchMainModule('practice')"
        >
          专项练习
        </div>
      </div>
    </header>

    <div class="main-body">
      <!-- 左侧分类侧边栏（对接你的分数据库） -->
      <aside class="sidebar">
        <div class="sidebar-title">
          {{ mainModule === 'term' ? '术语分类' : mainModule === 'video' ? '视频分类' : '功能菜单' }}
        </div>
        <ul class="category-list">
          <li 
            class="category-item"
            :class="{ active: currentCategory === 'all' }"
            @click="selectCategory('all')"
          >
            全部术语
          </li>
          <li 
            class="category-item"
            v-for="cat in categoryList" 
            :key="cat.id"
            :class="{ active: currentCategory === cat.id }"
            @click="selectCategory(cat.id)"
          >
            {{ cat.icon }} {{ cat.name }}
            <span class="count">({{ getCategoryTermCount(cat.id) }})</span>
          </li>
        </ul>
      </aside>

      <!-- 右侧主内容区 -->
      <main class="content-wrap">
        <!-- 1. 术语记忆模块 -->
        <div v-if="mainModule === 'term'" class="term-study">
          <div class="content-desc">
            当前数据库：{{ getCurrentCategoryName() }}
            ｜ 共 {{ filterTermList.length }} 条术语
          </div>

          <!-- 记忆卡片 -->
          <div class="card-box" @click="flipCard">
            <div class="term-card" :class="{ flipped: isFlipped }">
              <div class="card-front">
                <div class="word">{{ currentTerm.name || '暂无数据' }}</div>
                <div class="tip">点击卡片查看释义</div>
              </div>
              <div class="card-back">
                <div class="line"><strong>英文：</strong>{{ currentTerm.enName || '无' }}</div>
                <div class="line"><strong>俄文：</strong>{{ currentTerm.ruName || '无' }}</div>
                <div class="line"><strong>解释：</strong>{{ currentTerm.desc || '暂无' }}</div>
                <div class="line tag">所属数据库：{{ getCategoryName(currentTerm.category) }}</div>
              </div>
            </div>
          </div>

          <!-- 状态标记按钮 -->
          <div class="mark-btns">
            <el-button size="small" @click.stop="markStatus('master')">✅ 已掌握</el-button>
            <el-button size="small" @click.stop="markStatus('weak')">⚠️ 未掌握</el-button>
            <el-button size="small" type="danger" @click.stop="markStatus('wrong')">❌ 加入错题</el-button>
          </div>

          <!-- 翻页按钮 -->
          <div class="btn-group">
            <el-button @click="prevTerm" size="large">上一张</el-button>
            <el-button type="primary" @click="randomTerm" size="large">随机换题</el-button>
            <el-button @click="nextTerm" size="large">下一张</el-button>
          </div>

          <div class="count-info">
            当前第 {{ nowIndex + 1 }} / 共 {{ filterTermList.length }} 条
          </div>
        </div>

        <!-- 2. 视频学习模块 -->
        <div v-if="mainModule === 'video'" class="video-study">
          <div class="content-desc">
            当前分类：{{ currentCategory === 'all' ? '全部分类' : getCategoryName(currentCategory) }}
            ｜ 共 {{ filterVideoList.length }} 个视频
          </div>

          <div v-if="filterVideoList.length" class="video-card-list">
            <div 
              class="video-card"
              v-for="(video, idx) in filterVideoList" 
              :key="video.id"
              @click="playVideo(idx)"
              :class="{ active: currentVideoIdx === idx }"
            >
              <div class="video-poster">
                <img :src="video.poster" alt="封面">
                <div class="play-icon">▶</div>
              </div>
              <div class="video-name">{{ video.name }}</div>
              <div class="video-cate">{{ getCategoryName(video.categoryId) }}</div>
            </div>
          </div>

          <!-- 视频播放区 -->
          <div v-if="currentVideo" class="video-player">
            <h4>{{ currentVideo.name }}</h4>
            <video controls width="100%" :poster="currentVideo.poster">
              <source :src="currentVideo.src" type="video/mp4">
              浏览器不支持视频播放
            </video>
            <div class="video-term-box">
              <p>📖 关联术语：</p>
              <span 
                class="term-tag" 
                v-for="tid in currentVideo.termIds" 
                :key="tid"
                @click="jumpToTerm(tid)"
              >
                {{ getTermName(tid) }}
              </span>
            </div>
          </div>

          <div v-else class="empty-tip">该分类下暂无视频</div>
        </div>

        <!-- 3. 错题回顾模块 -->
        <div v-if="mainModule === 'wrong'" class="wrong-study">
          <div class="content-desc">错题集 ｜ 共 {{ wrongList.length }} 条</div>
          <div v-if="wrongList.length">
            <div class="card-box" @click="flipCard">
              <div class="term-card" :class="{ flipped: isFlipped }">
                <div class="card-front">
                  <div class="word">{{ currentWrong.name }}</div>
                  <div class="tip">点击查看释义</div>
                </div>
                <div class="card-back">
                  <div class="line"><strong>英文：</strong>{{ currentWrong.enName || '无' }}</div>
                  <div class="line"><strong>俄文：</strong>{{ currentWrong.ruName || '无' }}</div>
                  <div class="line"><strong>解释：</strong>{{ currentWrong.desc || '暂无' }}</div>
                </div>
              </div>
            </div>
            <div class="btn-group">
              <el-button @click="prevWrong" size="large">上一题</el-button>
              <el-button @click="nextWrong" size="large">下一题</el-button>
              <el-button type="success" @click="clearWrong" size="large">清空错题</el-button>
            </div>
            <div class="count-info">错题 {{ wrongIndex + 1 }} / {{ wrongList.length }}</div>
          </div>
          <div v-else class="empty-tip">暂无错题，继续加油！</div>
        </div>

        <!-- 4. 专项练习模块 -->
        <div v-if="mainModule === 'practice'" class="practice-study">
          <div class="content-desc">术语默写练习</div>
          <div class="practice-card">
            <div class="p-title">请翻译：{{ practiceTerm.name }}</div>
            <div class="p-inputs">
              <el-input
                v-model="inputEn"
                placeholder="输入英文翻译"
                @keyup.enter="checkPractice"
              />
              <el-input
                v-model="inputRu"
                placeholder="输入俄文翻译"
                class="mt10"
                @keyup.enter="checkPractice"
              />
            </div>
            <div class="p-btns">
              <el-button @click="checkPractice" type="primary">提交答案</el-button>
              <el-button @click="nextPractice">下一题</el-button>
            </div>
            <div class="p-result" :class="{ show: resultShow }">
              <p v-if="isRight">✅ 回答正确！</p>
              <p v-else>
                ❌ 正确答案：<br/>
                英文：{{ practiceTerm.enName || '无' }}<br/>
                俄文：{{ practiceTerm.ruName || '无' }}
              </p>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'

// ===================== 关键：和你的分数据库系统共用存储KEY =====================
const STORAGE_KEY = {
  category: 'sutix-category-list',  // 和你分数据库页面的分类KEY保持一致
  term: 'sutix-term-list',          // 和你分数据库页面的术语KEY保持一致
  video: 'study_video_list',
  wrong: 'study_wrong_list'
}

// ===================== 页面状态 =====================
const mainModule = ref('term')
const currentCategory = ref('all')
const isFlipped = ref(false)

// ===================== 从你的分数据库读取数据 =====================
const categoryList = ref([])
const termList = ref([])

// 读取分类（和你分数据库页面完全同步）
const getCategoryList = () => {
  const saved = localStorage.getItem(STORAGE_KEY.category)
  if (saved) {
    return JSON.parse(saved)
  }
  return []
}

// 读取术语（和你分数据库页面完全同步）
const getTermList = () => {
  const saved = localStorage.getItem(STORAGE_KEY.term)
  if (saved) {
    return JSON.parse(saved)
  }
  return []
}

// 筛选后的术语列表（按分类过滤）
const filterTermList = computed(() => {
  if (currentCategory.value === 'all') return termList.value
  return termList.value.filter(item => item.category === currentCategory.value)
})

// ===================== 工具方法（和你的系统逻辑一致） =====================
// 根据分类ID获取分类名称
const getCategoryName = (cid) => {
  const cate = categoryList.value.find(item => item.id === cid)
  return cate ? cate.name : '未分类'
}

// 获取指定分类的术语数量（和你分数据库页面逻辑一致）
const getCategoryTermCount = (categoryId) => {
  return termList.value.filter(term => term.category === categoryId).length
}

// 获取当前选中分类的名称
const getCurrentCategoryName = () => {
  if (currentCategory.value === 'all') return '全部数据库'
  const cate = categoryList.value.find(c => c.id === currentCategory.value)
  return cate ? cate.name : '未知数据库'
}

// ===================== 术语卡片状态 =====================
const nowIndex = ref(0)
const currentTerm = ref({})

// ===================== 视频数据 =====================
const videoList = ref([])
const currentVideoIdx = ref(-1)
const currentVideo = ref(null)
const filterVideoList = computed(() => {
  if (currentCategory.value === 'all') return videoList.value
  return videoList.value.filter(item => item.categoryId === currentCategory.value)
})
const getVideoList = () => {
  const data = localStorage.getItem(STORAGE_KEY.video)
  if (data) return JSON.parse(data)
  return []
}

// ===================== 错题数据 =====================
const wrongList = ref([])
const wrongIndex = ref(0)
const currentWrong = ref({})
const getWrongList = () => {
  const data = localStorage.getItem(STORAGE_KEY.wrong)
  return data ? JSON.parse(data) : []
}

// ===================== 专项练习 =====================
const practiceTerm = ref({})
const inputEn = ref('')
const inputRu = ref('')
const resultShow = ref(false)
const isRight = ref(false)

// ===================== 初始化 =====================
const initAll = () => {
  // 读取和你分数据库页面完全一致的数据
  categoryList.value = getCategoryList()
  termList.value = getTermList()
  videoList.value = getVideoList()
  wrongList.value = getWrongList()

  // 初始化当前术语
  if (filterTermList.value.length) {
    currentTerm.value = filterTermList.value[0]
    practiceTerm.value = filterTermList.value[0]
  }
  if (wrongList.value.length) {
    currentWrong.value = wrongList.value[0]
  }
}

// ===================== 模块 & 分类切换 =====================
const switchMainModule = (mod) => {
  mainModule.value = mod
  currentCategory.value = 'all'
  isFlipped.value = false
  currentVideo.value = null
  currentVideoIdx.value = -1
}
const selectCategory = (cid) => {
  currentCategory.value = cid
  isFlipped.value = false
  nowIndex.value = 0
  currentVideo.value = null
  currentVideoIdx.value = -1
  if (filterTermList.value.length) {
    currentTerm.value = filterTermList.value[0]
  }
}

// ===================== 术语卡片操作 =====================
const flipCard = () => {
  isFlipped.value = !isFlipped.value
}
const prevTerm = () => {
  isFlipped.value = false
  const list = filterTermList.value
  if (!list.length) return
  nowIndex.value = nowIndex.value <= 0 ? list.length - 1 : nowIndex.value - 1
  currentTerm.value = list[nowIndex.value]
}
const nextTerm = () => {
  isFlipped.value = false
  const list = filterTermList.value
  if (!list.length) return
  nowIndex.value = nowIndex.value >= list.length - 1 ? 0 : nowIndex.value + 1
  currentTerm.value = list[nowIndex.value]
}
const randomTerm = () => {
  isFlipped.value = false
  const list = filterTermList.value
  const max = list.length
  if (max <= 1) return
  let rnd = Math.floor(Math.random() * max)
  while (rnd === nowIndex.value) rnd = Math.floor(Math.random() * max)
  nowIndex.value = rnd
  currentTerm.value = list[rnd]
}
const markStatus = (type) => {
  const item = currentTerm.value
  if (!item.id) return
  if (type === 'wrong') {
    const exist = wrongList.value.find(i => i.id === item.id)
    if (!exist) {
      wrongList.value.push(item)
      localStorage.setItem(STORAGE_KEY.wrong, JSON.stringify(wrongList.value))
      ElMessage.success('已加入错题本')
    } else {
      ElMessage.info('该术语已在错题本')
    }
  } else {
    ElMessage.success('状态标记成功')
  }
}

// ===================== 视频操作 =====================
const playVideo = (idx) => {
  currentVideoIdx.value = idx
  currentVideo.value = filterVideoList.value[idx]
}
const getTermName = (tid) => {
  const term = termList.value.find(t => t.id === tid)
  return term ? term.name : '未知术语'
}
const jumpToTerm = (tid) => {
  mainModule.value = 'term'
  const term = termList.value.find(t => t.id === tid)
  if (term) {
    currentCategory.value = term.category
    const list = filterTermList.value
    const idx = list.findIndex(t => t.id === tid)
    nowIndex.value = idx
    currentTerm.value = list[idx]
  }
}

// ===================== 错题操作 =====================
const prevWrong = () => {
  isFlipped.value = false
  wrongIndex.value = wrongIndex.value <= 0 ? wrongList.value.length - 1 : wrongIndex.value - 1
  currentWrong.value = wrongList.value[wrongIndex.value]
}
const nextWrong = () => {
  isFlipped.value = false
  wrongIndex.value = wrongIndex.value >= wrongList.value.length - 1 ? 0 : wrongIndex.value + 1
  currentWrong.value = wrongList.value[wrongIndex.value]
}
const clearWrong = () => {
  wrongList.value = []
  wrongIndex.value = 0
  localStorage.setItem(STORAGE_KEY.wrong, JSON.stringify([]))
  ElMessage.success('错题本已清空')
}

// ===================== 专项练习 =====================
const nextPractice = () => {
  resultShow.value = false
  inputEn.value = ''
  inputRu.value = ''
  const list = termList.value
  if (!list.length) return
  practiceTerm.value = list[Math.floor(Math.random() * list.length)]
}
const checkPractice = () => {
  const enAns = inputEn.value.trim().toLowerCase()
  const ruAns = inputRu.value.trim().toLowerCase()
  const enStd = (practiceTerm.value.enName || '').toLowerCase()
  const ruStd = (practiceTerm.value.ruName || '').toLowerCase()

  isRight.value = enAns === enStd && ruAns === ruStd
  resultShow.value = true

  if (isRight.value) {
    ElMessage.success('回答正确！')
    setTimeout(nextPractice, 1200)
  } else {
    ElMessage.error('答案有误，请重新尝试')
  }
}

onMounted(() => {
  initAll()
})
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}
.app-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f7fa;
  font-family: "Microsoft Yahei", sans-serif;
}

/* 顶部导航栏 */
.top-header {
  height: 64px;
  background: #ffffff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  display: flex;
  align-items: center;
  padding: 0 40px;
  position: sticky;
  top: 0;
  z-index: 99;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-right: 60px;
}
.main-nav {
  display: flex;
  gap: 8px;
}
.nav-item {
  padding: 0 20px;
  height: 64px;
  line-height: 64px;
  font-size: 16px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}
.nav-item:hover {
  color: #409eff;
}
.nav-item.active {
  color: #409eff;
  border-bottom: 3px solid #409eff;
  font-weight: 500;
}

/* 主体布局：侧边栏 + 内容区 */
.main-body {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  gap: 20px;
}

/* 左侧分类侧边栏 */
.sidebar {
  width: 220px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  overflow: hidden;
  flex-shrink: 0;
}
.sidebar-title {
  height: 48px;
  line-height: 48px;
  padding: 0 20px;
  background: #409eff;
  color: #fff;
  font-size: 16px;
  font-weight: 500;
}
.category-list {
  list-style: none;
  padding: 10px 0;
}
.category-item {
  padding: 12px 20px;
  font-size: 15px;
  color: #555;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.category-item:hover {
  background: #f0f7ff;
  color: #409eff;
}
.category-item.active {
  background: #e5f0ff;
  color: #409eff;
  border-right: 4px solid #409eff;
}
.count {
  font-size: 12px;
  color: #999;
}

/* 右侧主内容区 */
.content-wrap {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 30px;
  min-height: calc(100vh - 144px);
}
.content-desc {
  font-size: 15px;
  color: #666;
  margin-bottom: 30px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
.empty-tip {
  text-align: center;
  padding: 80px 0;
  color: #999;
  font-size: 16px;
}

/* ========== 术语卡片样式 ========== */
.term-study {
  text-align: center;
}
.card-box {
  width: 520px;
  height: 320px;
  margin: 0 auto 30px;
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
  box-shadow: 0 4px 20px rgba(0,0,0,0.12);
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
  text-align: left;
}
.word {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 20px;
}
.tip {
  font-size: 14px;
  opacity: 0.8;
}
.line {
  font-size: 16px;
  margin: 10px 0;
  width: 100%;
}
.tag {
  margin-top: 20px;
  color: #999;
  font-size: 14px;
}
.mark-btns {
  margin-bottom: 25px;
  display: flex;
  gap: 12px;
  justify-content: center;
}
.btn-group {
  margin: 25px 0;
  display: flex;
  gap: 18px;
  justify-content: center;
}
.count-info {
  font-size: 15px;
  color: #666;
}

/* ========== 视频模块样式 ========== */
.video-card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}
.video-card {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}
.video-card:hover {
  box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  transform: translateY(-4px);
}
.video-card.active {
  border-color: #409eff;
}
.video-poster {
  position: relative;
  height: 135px;
  overflow: hidden;
}
.video-poster img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.play-icon {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  line-height: 40px;
  text-align: center;
  border-radius: 50%;
  background: rgba(0,0,0,0.5);
  color: #fff;
  font-size: 18px;
}
.video-name {
  padding: 12px 10px 4px;
  font-size: 15px;
  color: #333;
}
.video-cate {
  padding: 0 10px 12px;
  font-size: 12px;
  color: #999;
}
.video-player {
  margin-top: 20px;
}
.video-player h4 {
  margin-bottom: 15px;
  color: #333;
}
.video-term-box {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px dashed #eee;
}
.term-tag {
  display: inline-block;
  padding: 4px 12px;
  background: #f5f7fa;
  border-radius: 15px;
  margin: 6px 8px 6px 0;
  font-size: 14px;
  cursor: pointer;
}
.term-tag:hover {
  background: #e5f0ff;
  color: #409eff;
}

/* ========== 练习模块样式 ========== */
.practice-study {
  max-width: 600px;
  margin: 0 auto;
}
.practice-card {
  background: #f9fafb;
  padding: 35px;
  border-radius: 12px;
  border: 1px solid #eee;
}
.p-title {
  font-size: 22px;
  font-weight: 500;
  text-align: center;
  margin-bottom: 25px;
  color: #333;
}
.p-inputs {
  margin-bottom: 25px;
}
.mt10 {
  margin-top: 10px;
}
.p-btns {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-bottom: 20px;
}
.p-result {
  padding: 15px;
  border-radius: 8px;
  background: #fff;
  display: none;
}
.p-result.show {
  display: block;
}

/* 响应式适配 */
@media (max-width: 992px) {
  .main-body {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
  }
  .card-box {
    width: 100%;
  }
}
</style>
