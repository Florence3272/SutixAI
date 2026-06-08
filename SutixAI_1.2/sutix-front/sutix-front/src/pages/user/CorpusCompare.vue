<template>
  <div class="corpus-page">
    <!-- 左侧纵轴：数据库导航 -->
    <div class="corpus-sidebar">
      <div class="sidebar-title">专业数据库</div>
      <el-menu
        v-model="activeDb"
        mode="vertical"
        class="sidebar-menu"
        @select="handleDbChange"
      >
        <el-menu-item index="all">
          <span class="menu-text">📚 全部语料</span>
        </el-menu-item>
        <el-menu-item index="mechanical">
          <span class="menu-text">⚙️ 机械工程</span>
        </el-menu-item>
        <el-menu-item index="electrical">
          <span class="menu-text">⚡ 电气工程</span>
        </el-menu-item>
        <el-menu-item index="chemical">
          <span class="menu-text">🧪 化学化工</span>
        </el-menu-item>
        <el-menu-item index="medical">
          <span class="menu-text">💊 医药卫生</span>
        </el-menu-item>
        <el-menu-item index="computer">
          <span class="menu-text">💻 计算机科学</span>
        </el-menu-item>
        <el-menu-item index="economic">
          <span class="menu-text">💰 经济贸易</span>
        </el-menu-item>
      </el-menu>
    </div>

    <!-- 右侧主体 -->
    <div class="corpus-content">
      <!-- 顶部横轴：文献标签 -->
      <div class="doc-tab-wrapper">
        <el-tabs v-model="activeDoc" @tab-click="handleDocChange">
          <el-tab-pane
            v-for="item in filteredDocList"
            :key="item.id"
            :name="item.id"
            :label="item.name"
          />
        </el-tabs>
      </div>

      <!-- 双语对照区 -->
      <div class="bilingual-wrapper">
        <!-- 中文 -->
        <div class="col col-zh">
          <div
            v-for="(line, idx) in currentLines"
            :key="idx"
            class="line"
            :class="{ active: hoverIdx === idx }"
            @mouseenter="hoverIdx = idx"
            @mouseleave="hoverIdx = -1"
          >
            <span v-html="renderMarkedZh(line.zh)"></span>
          </div>
        </div>

        <!-- 俄文 -->
        <div class="col col-ru">
          <div
            v-for="(line, idx) in currentLines"
            :key="idx"
            class="line"
            :class="{ active: hoverIdx === idx }"
            @mouseenter="hoverIdx = idx"
            @mouseleave="hoverIdx = -1"
          >
            <span v-html="renderMarkedRu(line.ru)"></span>
          </div>
        </div>
      </div>
    </div>

    <!-- 术语悬浮气泡提示 -->
    <div
      class="term-tip"
      :style="tipStyle"
      v-if="showTip && currentTerm"
      @mouseleave="hideTip"
    >
      <div class="tip-zh">中文：{{ currentTerm.name }}</div>
      <div class="tip-ru">俄文：{{ currentTerm.ruName }}</div>
      <div class="tip-desc">点击查看详情</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeDb = ref('all')
const activeDoc = ref('doc01')
const hoverIdx = ref(-1)

// 术语库数据
const termList = ref([])
// 悬浮提示控制
const showTip = ref(false)
const currentTerm = ref(null)
const tipStyle = ref({})

// 文献列表
const docList = ref([
  { id: 'doc01', name: '智能制造概述', db: ['all', 'mechanical'] },
  { id: 'doc02', name: '电力系统基础', db: ['all', 'electrical'] },
  { id: 'doc03', name: 'AI术语规范', db: ['all', 'computer'] },
])

// 语料数据
const corpusData = ref({
  doc01: [
    { zh: '智能制造是基于新一代信息技术的先进制造过程。', ru: 'Интеллектуальное производство — это передовой производственный процесс на основе новых информационных технологий.' },
    { zh: '自动化设备大幅提升了生产效率与产品质量。', ru: 'Автоматизированное оборудование значительно повышает производительность и качество продукции.' },
    { zh: '工业互联网实现设备之间的数据互通与协同作业。', ru: 'Промышленный интернет обеспечивает обмен данными и совместную работу между устройствами.' },
  ],
  doc02: [
    { zh: '电力系统由发电、输电、配电和用电环节组成。', ru: 'Энергетическая система состоит из генерации, передачи, распределения и потребления электроэнергии.' },
    { zh: '高压输电线路能够减少远距离传输中的能量损耗。', ru: 'Линии высоковольтной передачи снижают потери энергии при дистанционной транспортировке.' },
  ],
  doc03: [
    { zh: '人工智能是研究使计算机模拟人类智能的技术科学。', ru: 'Искусственный интеллект — это техническая наука, исследующая моделирование человеческого интеллекта компьютерами.' },
  ],
})

// 加载本地术语
const loadTerms = () => {
  try {
    const data = localStorage.getItem('sutix-term-list')
    if (data) termList.value = JSON.parse(data)
  } catch (e) {}
}

// 根据文本匹配术语对象
const getTermByText = (text) => {
  return termList.value.find(t => t.name === text || t.ruName === text)
}

// 跳转术语详情
const goToTerm = (text) => {
  const target = getTermByText(text)
  if (!target) return
  const idx = termList.value.findIndex(t => t.name === target.name && t.ruName === target.ruName)
  if (idx !== -1) router.push(`/term-detail?index=${idx}`)
}

// 显示悬浮提示
const showTermTip = (e, text) => {
  const target = getTermByText(text)
  if (!target) return
  currentTerm.value = target
  // 定位气泡，避免超出视口
  const x = e.pageX + 15
  const y = e.pageY + 15
  tipStyle.value = {
    left: `${x}px`,
    top: `${y}px`
  }
  showTip.value = true
}

// 隐藏提示
const hideTip = () => {
  showTip.value = false
  currentTerm.value = null
}

// 渲染中文高亮术语（绑定悬浮+点击事件）
const renderMarkedZh = (text) => {
  let res = text
  // 按字符长度倒序匹配，避免短词优先拦截长词
  const sortTerms = [...termList.value].sort((a, b) => b.name.length - a.name.length)
  sortTerms.forEach(term => {
    const reg = new RegExp(`(${term.name})`, 'g')
    res = res.replace(reg, `
      <span 
        class="term-tag" 
        onmouseover="handleTipOver(event,'${term.name}')"
        onmouseout="handleTipOut()"
        onclick="handleTermClick('${term.name}')"
      >$1</span>
    `)
  })
  return res
}

// 渲染俄文高亮术语
const renderMarkedRu = (text) => {
  let res = text
  const sortTerms = [...termList.value].sort((a, b) => b.ruName.length - a.ruName.length)
  sortTerms.forEach(term => {
    const reg = new RegExp(`(${term.ruName})`, 'g')
    res = res.replace(reg, `
      <span 
        class="term-tag" 
        onmouseover="handleTipOver(event,'${term.ruName}')"
        onmouseout="handleTipOut()"
        onclick="handleTermClick('${term.ruName}')"
      >$1</span>
    `)
  })
  return res
}

// 计算属性
const filteredDocList = computed(() => {
  return docList.value.filter(item => item.db.includes(activeDb.value))
})
const currentLines = computed(() => {
  return corpusData.value[activeDoc.value] || []
})

// 切换数据库
const handleDbChange = (val) => {
  activeDb.value = val
  const list = filteredDocList.value
  if (list.length) activeDoc.value = list[0].id
}

// 切换文献
const handleDocChange = (tab) => {
  activeDoc.value = tab.name
  hoverIdx.value = -1
  hideTip()
}

// 挂载全局方法，供HTML内联事件调用
onMounted(() => {
  loadTerms()
  window.handleTermClick = goToTerm
  window.handleTipOver = (e, text) => showTermTip(e, text)
  window.handleTipOut = hideTip
})

// 卸载时清除全局方法
onUnmounted(() => {
  delete window.handleTermClick
  delete window.handleTipOver
  delete window.handleTipOut
})
</script>

<style scoped>
/* 整体布局 */
.corpus-page {
  display: flex;
  height: calc(100vh - 60px);
  background: #f7f8fa;
  overflow: hidden;
  position: relative;
}

/* 左侧侧边栏 */
.corpus-sidebar {
  width: 210px;
  background: #ffffff;
  border-right: 1px solid #e5e6eb;
  display: flex;
  flex-direction: column;
}
.sidebar-title {
  padding: 18px 16px 10px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}
.sidebar-menu {
  border-right: none;
  flex: 1;
}
.sidebar-menu :deep(.el-menu-item) {
  height: 44px;
  line-height: 44px;
  font-size: 14px;
}
.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #e8f3ff;
  color: #409eff;
  font-weight: 500;
}

/* 右侧内容 */
.corpus-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部文献标签 */
.doc-tab-wrapper {
  background: #fff;
  padding: 0 20px;
  border-bottom: 1px solid #e5e6eb;
}
.doc-tab-wrapper :deep(.el-tabs__header) {
  margin: 0;
}
.doc-tab-wrapper :deep(.el-tabs__nav-wrap) {
  overflow-x: auto;
}

/* 双语对照 */
.bilingual-wrapper {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.col {
  width: 50%;
  padding: 24px 32px;
  font-size: 15px;
  line-height: 1.75;
  overflow-y: auto;
}
.col-zh {
  background: #ffffff;
  border-right: 1px solid #e5e6eb;
}
.col-ru {
  background: #fcfcfc;
}

/* 句子行 */
.line {
  padding: 10px 14px;
  margin-bottom: 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.line:hover {
  background: #f2f3f5;
}
.line.active {
  background: #fff0f0;
  color: #d93025;
}

/* 术语高亮样式 */
:deep(.term-tag) {
  color: #409eff !important;
  font-weight: 500;
  text-decoration: underline;
  text-underline-offset: 3px;
  cursor: pointer;
  transition: color 0.2s;
}
:deep(.term-tag:hover) {
  color: #1677ff !important;
}

/* 术语悬浮气泡 */
.term-tip {
  position: fixed;
  z-index: 9999;
  min-width: 200px;
  max-width: 320px;
  padding: 12px 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  border: 1px solid #ebeef5;
  font-size: 14px;
  line-height: 1.6;
  pointer-events: auto;
}
.tip-zh {
  color: #333;
  margin-bottom: 4px;
}
.tip-ru {
  color: #666;
  margin-bottom: 6px;
}
.tip-desc {
  font-size: 12px;
  color: #999;
  border-top: 1px dashed #eee;
  padding-top: 6px;
}

/* 滚动条美化 */
.col::-webkit-scrollbar {
  width: 6px;
}
.col::-webkit-scrollbar-thumb {
  background: #d0d3d9;
  border-radius: 3px;
}
</style>
