<template>
  <div class="corpus-page">
    <!-- 左侧纵轴：数据库导航 -->
    <div class="corpus-sidebar">
      <div class="sidebar-title">专业数据库</div>
      <el-menu
        :default-active="activeDb + ''"
        mode="vertical"
        class="sidebar-menu"
        @select="handleDbChange"
      >
        <el-menu-item index="all">
          <span class="menu-text">📚 全部语料</span>
        </el-menu-item>
        <el-menu-item
          v-for="cat in categoryList"
          :key="cat.id"
          :index="String(cat.id)"
        >
          <span class="menu-text">{{ cat.icon }} {{ cat.name }}</span>
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
            :name="String(item.id)"
            :label="item.name"
          />
        </el-tabs>
      </div>

      <!-- 双语对照区 -->
      <div class="bilingual-wrapper">
        <!-- 中文 -->
        <div class="col col-zh">
          <div v-if="currentLines.length === 0" class="empty-tip">该文档暂无语料数据</div>
          <div
            v-for="(line, idx) in currentLines"
            :key="idx"
            class="line"
            :class="{ active: hoverIdx === idx }"
            @mouseenter="hoverIdx = idx"
            @mouseleave="hoverIdx = -1"
          >
            <span v-html="renderMarkedZh(line.zhText)"></span>
          </div>
        </div>

        <!-- 俄文 -->
        <div class="col col-ru">
          <div v-if="currentLines.length === 0" class="empty-tip">Нет данных</div>
          <div
            v-for="(line, idx) in currentLines"
            :key="idx"
            class="line"
            :class="{ active: hoverIdx === idx }"
            @mouseenter="hoverIdx = idx"
            @mouseleave="hoverIdx = -1"
          >
            <span v-html="renderMarkedRu(line.ruText)"></span>
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
import { termApi } from '@/api/termApi'
import { termCategoryApi } from '@/api/termCategoryApi'
import { corpusApi } from '@/api/corpusApi'

const router = useRouter()
const activeDb = ref('all')
const activeDoc = ref('')
const hoverIdx = ref(-1)

const categoryList = ref([])
const docList = ref([])
const documentSentences = ref({})   // { docId: [{ zhText, ruText, seq }, ...] }
const termList = ref([])
const showTip = ref(false)
const currentTerm = ref(null)
const tipStyle = ref({})

// 加载分类
const loadCategories = async () => {
  try {
    const res = await termCategoryApi.listAll()
    categoryList.value = res.data.map(item => ({
      id: item.id,
      name: item.categoryName,
      icon: item.icon || '📚'
    }))
  } catch (e) { console.error('加载分类失败', e) }
}

// 加载术语（用于高亮匹配）
const loadTerms = async () => {
  try {
    const res = await termApi.pageList({ page: 1, size: 999, keyword: '' })
    termList.value = res.data.records || []
  } catch (e) { console.error('加载术语失败', e) }
}

// 加载文档列表
const loadDocuments = async () => {
  try {
    const categoryId = activeDb.value === 'all' ? null : Number(activeDb.value)
    const res = await corpusApi.listDocuments(categoryId)
    docList.value = res.data || []
    if (docList.value.length && !activeDoc.value) {
      activeDoc.value = String(docList.value[0].id)
      loadDocumentSentences(docList.value[0].id)
    }
  } catch (e) { console.error('加载文档失败', e) }
}

// 加载文档语料详情
const loadDocumentSentences = async (docId) => {
  try {
    const res = await corpusApi.getDocumentDetail(docId)
    documentSentences.value[docId] = res.data.sentences || []
  } catch (e) {
    console.error('加载语料失败', e)
    documentSentences.value[docId] = []
  }
}

// 计算属性
const filteredDocList = computed(() => docList.value)

const currentLines = computed(() => {
  const lines = documentSentences.value[activeDoc.value]
  return lines || []
})

// 切换数据库
const handleDbChange = (val) => {
  activeDb.value = val
  activeDoc.value = ''
  documentSentences.value = {}
  loadDocuments()
}

// 切换文献
const handleDocChange = (tab) => {
  activeDoc.value = tab.props.name
  hoverIdx.value = -1
  hideTip()
  if (!documentSentences.value[activeDoc.value]) {
    loadDocumentSentences(Number(activeDoc.value))
  }
}

// 根据文本匹配术语对象
const getTermByText = (text) => {
  return termList.value.find(t => t.name === text || t.ruName === text)
}

// 跳转术语详情
const goToTerm = (text) => {
  const target = getTermByText(text)
  if (!target) return
  router.push(`/term-detail?termId=${target.id}`)
}

// 显示悬浮提示
const showTermTip = (e, text) => {
  const target = getTermByText(text)
  if (!target) return
  currentTerm.value = target
  tipStyle.value = {
    left: `${e.pageX + 15}px`,
    top: `${e.pageY + 15}px`
  }
  showTip.value = true
}

const hideTip = () => {
  showTip.value = false
  currentTerm.value = null
}

// 逃逸 HTML 特殊字符
const escapeHtml = (text) => {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
}

// 渲染中文高亮术语
const renderMarkedZh = (text) => {
  let res = escapeHtml(text)
  const sorted = [...termList.value].sort((a, b) => (b.name || '').length - (a.name || '').length)
  sorted.forEach(term => {
    if (!term.name) return
    const escaped = term.name.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    const reg = new RegExp(`(${escaped})`, 'g')
    res = res.replace(reg, `<span class="term-tag" onmouseover="window._tipOver(event,'${term.name.replace(/'/g, "\\'")}')" onmouseout="window._tipOut()" onclick="window._termClick('${term.name.replace(/'/g, "\\'")}')">$1</span>`)
  })
  return res
}

// 渲染俄文高亮术语
const renderMarkedRu = (text) => {
  let res = escapeHtml(text)
  const sorted = [...termList.value].sort((a, b) => (b.ruName || '').length - (a.ruName || '').length)
  sorted.forEach(term => {
    if (!term.ruName) return
    const escaped = term.ruName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
    const reg = new RegExp(`(${escaped})`, 'g')
    res = res.replace(reg, `<span class="term-tag" onmouseover="window._tipOver(event,'${term.ruName.replace(/'/g, "\\'")}')" onmouseout="window._tipOut()" onclick="window._termClick('${term.ruName.replace(/'/g, "\\'")}')">$1</span>`)
  })
  return res
}

onMounted(async () => {
  await loadCategories()
  await loadTerms()
  await loadDocuments()

  // 注册全局方法
  window._termClick = goToTerm
  window._tipOver = showTermTip
  window._tipOut = hideTip
})

onUnmounted(() => {
  delete window._termClick
  delete window._tipOver
  delete window._tipOut
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
.sidebar-menu { border-right: none; flex: 1; }
.sidebar-menu :deep(.el-menu-item) { height: 44px; line-height: 44px; font-size: 14px; }
.sidebar-menu :deep(.el-menu-item.is-active) { background-color: #e8f3ff; color: #409eff; font-weight: 500; }

.corpus-content { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
.doc-tab-wrapper { background: #fff; padding: 0 20px; border-bottom: 1px solid #e5e6eb; }
.doc-tab-wrapper :deep(.el-tabs__header) { margin: 0; }
.doc-tab-wrapper :deep(.el-tabs__nav-wrap) { overflow-x: auto; }

.bilingual-wrapper { display: flex; flex: 1; overflow: hidden; }
.col { width: 50%; padding: 24px 32px; font-size: 15px; line-height: 1.75; overflow-y: auto; }
.col-zh { background: #ffffff; border-right: 1px solid #e5e6eb; }
.col-ru { background: #fcfcfc; }
.empty-tip { text-align: center; padding-top: 80px; color: #999; font-size: 16px; }

.line { padding: 10px 14px; margin-bottom: 8px; border-radius: 6px; cursor: pointer; transition: all 0.2s ease; }
.line:hover { background: #f2f3f5; }
.line.active { background: #fff0f0; color: #d93025; }

:deep(.term-tag) { color: #409eff !important; font-weight: 500; text-decoration: underline; text-underline-offset: 3px; cursor: pointer; transition: color 0.2s; }
:deep(.term-tag:hover) { color: #1677ff !important; }

.term-tip {
  position: fixed; z-index: 9999; min-width: 200px; max-width: 320px;
  padding: 12px 16px; background: #fff; border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15); border: 1px solid #ebeef5;
  font-size: 14px; line-height: 1.6; pointer-events: auto;
}
.tip-zh { color: #333; margin-bottom: 4px; }
.tip-ru { color: #666; margin-bottom: 6px; }
.tip-desc { font-size: 12px; color: #999; border-top: 1px dashed #eee; padding-top: 6px; }

.col::-webkit-scrollbar { width: 6px; }
.col::-webkit-scrollbar-thumb { background: #d0d3d9; border-radius: 3px; }
</style>
