<template>
  <div class="translator-page">
    <!-- 顶部功能栏 -->
    <div class="trans-header">
      <div class="title">📝 中俄文档翻译</div>
      <div class="switch-wrap">
        <span>翻译方向：</span>
        <el-radio-group v-model="transDirection" size="small">
          <el-radio label="zh2ru">中 → 俄</el-radio>
          <el-radio label="ru2zh">俄 → 中</el-radio>
        </el-radio-group>
        <el-button 
          icon="el-icon-sort" 
          size="small" 
          class="swap-btn"
          @click="swapLang"
        >互换语种</el-button>
      </div>
    </div>

    <!-- 操作主卡片 -->
    <div class="operate-card">
      <!-- 文档上传 -->
      <div class="upload-block">
        <el-upload
          ref="uploadRef"
          action="#"
          :auto-upload="false"
          :file-list="fileList"
          accept=".txt,.doc,.docx,.pdf"
          @change="handleFileChange"
          :on-exceed="handleExceed"
          limit="1"
          class="custom-upload"
          :disabled="loading"
        >
          <el-button type="primary" icon="el-icon-upload2" :disabled="loading">上传文档</el-button>
          <template #tip>
            <div class="upload-tip">支持格式：TXT / DOC / DOCX，单次仅可上传 1 个文件</div>
          </template>
        </el-upload>
        <el-button @click="clearFile" class="ml-3" :disabled="loading">清空文件</el-button>
      </div>

      <!-- 文本输入 + 字数统计 -->
      <div class="input-block">
        <div class="input-top">
          <span class="count-text">已输入：{{ sourceText.length }} 字符</span>
          <el-button icon="el-icon-close" size="small" text @click="sourceText = ''">清空</el-button>
        </div>
        <el-input
          v-model="sourceText"
          type="textarea"
          :rows="9"
          placeholder="请粘贴待翻译文本，或上传文档自动读取内容"
          resize="none"
          class="custom-textarea"
          :disabled="loading"
          @input="resultText = ''"
        />
      </div>

      <!-- 功能按钮 -->
      <div class="btn-block">
        <el-button 
          type="primary" 
          icon="el-icon-s-promotion" 
          @click="doTranslate" 
          :loading="loading"
          size="large"
        >
          开始翻译
        </el-button>
        <el-button icon="el-icon-delete" @click="clearAll" size="large" :disabled="loading">清空全部</el-button>
      </div>
    </div>

    <!-- 翻译结果区 -->
    <div class="result-card">
      <div class="result-head">
        <span class="result-title">翻译结果（左右对照）</span>
        <div class="result-actions">
          <el-button icon="el-icon-document-copy" size="small" @click="copyResult">复制译文</el-button>
          <el-button icon="el-icon-download" size="small" @click="exportTxt">导出TXT</el-button>
        </div>
      </div>

      <!-- 无结果空状态 -->
      <div class="empty-tip" v-if="!resultText">
        <div class="empty-icon">📄</div>
        <div class="empty-desc">翻译结果将展示在此处</div>
      </div>

      <!-- 对照区域（联动滚动） -->
      <div class="result-wrapper" v-else>
        <div class="result-col source-col" @scroll="syncScroll('source', $event)">
          <div class="col-header">原文</div>
          <div class="col-content">{{ sourceText }}</div>
        </div>
        <div class="result-col target-col" @scroll="syncScroll('target', $event)">
          <div class="col-header">译文</div>
          <div class="col-content" contenteditable>{{ resultText }}</div>
        </div>
      </div>
    </div>

    <!-- 翻译历史记录 -->
    <div class="history-card" v-if="historyList.length">
      <div class="history-head">📜 最近翻译记录（点击复用）</div>
      <div class="history-list">
        <div 
          class="history-item" 
          v-for="(item, idx) in historyList" 
          :key="idx"
          @click="reuseHistory(item)"
        >
          {{ (item.sourceText || '').slice(0, 40) }}{{ (item.sourceText || '').length > 40 ? '...' : '' }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { translationApi } from '@/api/translationApi'
import { termApi } from '@/api/termApi'

// 翻译方向（本地存储记忆）
const transDirection = ref(localStorage.getItem('transDir') || 'zh2ru')
// 文本数据
const sourceText = ref('')
const resultText = ref('')
// 状态
const loading = ref(false)
const polling = ref(false)
const currentTaskId = ref(null)
const fileList = ref([])
const uploadRef = ref(null)
// 滚动联动标记
const isSyncing = ref(false)
// 翻译历史记录
const historyList = ref([])
// 术语库
const termList = ref([])

// 监听翻译方向，自动本地存储
watch(transDirection, (val) => {
  localStorage.setItem('transDir', val)
})

// 加载术语库
const loadTerms = async () => {
  try {
    const res = await termApi.pageList({ page: 1, size: 999, keyword: '' })
    termList.value = res.data.records || []
  } catch (e) { console.error('加载术语失败', e) }
}

// 加载翻译历史
const loadHistory = async () => {
  try {
    const res = await translationApi.getHistory()
    historyList.value = res.data || []
  } catch (e) { console.error('加载翻译历史失败', e) }
}

// --------------- 文件上传 ---------------
const handleFileChange = (file) => {
  const fileName = file.name.toLowerCase()
  if (fileName.endsWith('.txt')) {
    const reader = new FileReader()
    reader.readAsText(file.raw)
    reader.onload = (e) => {
      sourceText.value = e.target.result
      resultText.value = ''
      ElMessage.success('文档读取成功')
    }
  } else if (fileName.endsWith('.doc') || fileName.endsWith('.docx') || fileName.endsWith('.pdf')) {
    // 上传到后端解析
    const formData = new FormData()
    formData.append('file', file.raw)
    loading.value = true
    translationApi.parseFile(formData).then(res => {
      if (res.code === 200 && res.data) {
        sourceText.value = res.data.text || ''
        resultText.value = ''
        ElMessage.success('文件解析成功')
      } else {
        ElMessage.error(res.msg || '文件解析失败')
      }
    }).catch(err => {
      ElMessage.error('文件解析失败：' + (err.message || '网络错误'))
    }).finally(() => {
      loading.value = false
    })
  } else {
    ElMessage.warning('不支持的文件格式，请上传 TXT / DOC / DOCX / PDF')
  }
}
const handleExceed = () => ElMessage.warning('最多只能上传 1 个文档')
const clearFile = () => {
  fileList.value = []
  uploadRef.value?.clearFiles()
}

// --------------- 核心翻译（术语优先 + API调用） ---------------
const doTranslate = async () => {
  const text = sourceText.value.trim()
  if (!text) return ElMessage.warning('请输入或上传待翻译内容')
  if (text.length > 5000) return ElMessage.warning('文本过长，请拆分后再翻译（最大5000字符）')

  loading.value = true
  resultText.value = ''

  try {
    // 1. 本地术语优先替换
    let dealText = text
    if (transDirection.value === 'zh2ru') {
      termList.value.forEach(term => {
        const reg = new RegExp(term.name, 'g')
        dealText = dealText.replace(reg, term.ruName)
      })
    } else {
      termList.value.forEach(term => {
        const reg = new RegExp(term.ruName, 'g')
        dealText = dealText.replace(reg, term.name)
      })
    }

    // 2. 提交翻译任务（异步）
    loading.value = true
    const res = await translationApi.translateText(dealText, transDirection.value)
    
    if (res.code !== 200 || !res.data) {
      throw new Error(res.msg || '翻译提交失败')
    }

    const taskId = res.data.id
    currentTaskId.value = taskId
    ElMessage.info('翻译已提交，正在等待结果...')

    // 3. 轮询翻译进度
    polling.value = true
    let result = null
    for (let i = 0; i < 60; i++) {
      await new Promise(r => setTimeout(r, 2000))
      const progressRes = await translationApi.getProgress(taskId)
      if (progressRes.code === 200 && progressRes.data) {
        const status = progressRes.data.status
        if (status === 1) {
          // 翻译成功
          result = progressRes.data.resultText || ''
          break
        } else if (status === -1) {
          throw new Error('翻译失败')
        }
        // status === 0 → 继续轮询
      }
    }
    polling.value = false

    if (result !== null) {
      resultText.value = result
      ElMessage.success('翻译完成')
    } else {
      throw new Error('翻译超时，请稍后查看翻译历史')
    }

    // 4. 刷新历史记录
    await loadHistory()
  } catch (err) {
    console.error(err)
    ElMessage.error('翻译失败：' + (err.message || '网络错误'))
  } finally {
    loading.value = false
    polling.value = false
  }
}

// --------------- 辅助功能 ---------------
// 清空全部
const clearAll = () => {
  sourceText.value = ''
  resultText.value = ''
  clearFile()
}

// 复制译文
const copyResult = async () => {
  if (!resultText.value) return ElMessage.warning('暂无译文')
  try {
    await navigator.clipboard.writeText(resultText.value)
    ElMessage.success('译文复制成功')
  } catch (err) {
    ElMessage.error('复制失败，请手动复制')
  }
}

// 导出TXT
const exportTxt = () => {
  if (!resultText.value) return ElMessage.warning('暂无译文')
  const content = `原文：\n${sourceText.value}\n\n译文：\n${resultText.value}`
  const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
  const a = document.createElement('a')
  a.href = URL.createObjectURL(blob)
  a.download = '翻译结果.txt'
  a.click()
  URL.revokeObjectURL(a.href)
}

// 语种互换
const swapLang = () => {
  const temp = sourceText.value
  sourceText.value = resultText.value
  resultText.value = temp
  transDirection.value = transDirection.value === 'zh2ru' ? 'ru2zh' : 'zh2ru'
}

// 左右联动滚动
const syncScroll = (type, e) => {
  if (isSyncing.value) return
  isSyncing.value = true
  const scrollTop = e.target.scrollTop
  const domList = document.querySelectorAll('.col-content')
  if (type === 'source') {
    domList[1].scrollTop = scrollTop
  } else {
    domList[0].scrollTop = scrollTop
  }
  setTimeout(() => isSyncing.value = false, 100)
}

// 复用历史记录
const reuseHistory = (item) => {
  sourceText.value = item.sourceText
  resultText.value = item.resultText || ''
}

onMounted(() => {
  loadTerms()
  loadHistory()
})
</script>

<style scoped>
/* 页面整体 */
.translator-page {
  width: 100%;
  min-height: calc(100vh - 60px);
  background-color: #f7f8fa;
  padding: 24px 32px;
  box-sizing: border-box;
}

/* 顶部栏 */
.trans-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  padding: 18px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  transition: box-shadow 0.2s;
}
.trans-header:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}
.trans-header .title {
  font-size: 19px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}
.switch-wrap {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #4b5563;
}
.swap-btn {
  padding: 4px 8px;
}

/* 操作卡片 */
.operate-card {
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  transition: box-shadow 0.2s;
}
.operate-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}

/* 上传区域 */
.upload-block {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}
:deep(.upload-tip) {
  font-size: 13px;
  color: #9ca3af;
  margin-top: 6px;
}

/* 输入区域 */
.input-block {
  margin-bottom: 24px;
}
.input-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.count-text {
  font-size: 13px;
  color: #6b7280;
}
:deep(.custom-textarea textarea) {
  font-size: 15px;
  line-height: 1.75;
  color: #333;
}

/* 按钮组 */
.btn-block {
  display: flex;
  gap: 14px;
}

/* 结果卡片 */
.result-card {
  background: #ffffff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
  transition: box-shadow 0.2s;
}
.result-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}
.result-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.result-title {
  font-size: 16px;
  font-weight: 500;
  color: #1f2937;
}
.result-actions {
  display: flex;
  gap: 8px;
}

/* 空状态 */
.empty-tip {
  text-align: center;
  padding: 60px 0;
  color: #9ca3af;
}
.empty-icon {
  font-size: 36px;
  margin-bottom: 12px;
  opacity: 0.6;
}
.empty-desc {
  font-size: 14px;
}

/* 双语对照 */
.result-wrapper {
  display: flex;
  gap: 20px;
}
.result-col {
  flex: 1;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;
}
.col-header {
  height: 38px;
  line-height: 38px;
  background-color: #f9fafb;
  text-align: center;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #e5e7eb;
}
.col-content {
  min-height: 240px;
  max-height: 400px;
  padding: 16px;
  font-size: 15px;
  line-height: 1.75;
  color: #333;
  white-space: pre-wrap;
  word-break: break-all;
  overflow-y: auto;
  outline: none;
}

/* 历史记录卡片 */
.history-card {
  background: #ffffff;
  padding: 20px 24px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}
.history-head {
  font-size: 15px;
  color: #333;
  margin-bottom: 12px;
}
.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.history-item {
  padding: 6px 12px;
  background: #f7f8fa;
  border-radius: 4px;
  font-size: 13px;
  color: #4b5563;
  cursor: pointer;
  max-width: 220px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  transition: all 0.2s;
}
.history-item:hover {
  background: #e8f3ff;
  color: #409eff;
}

/* 滚动条美化 */
.col-content::-webkit-scrollbar {
  width: 6px;
}
.col-content::-webkit-scrollbar-thumb {
  background-color: #d1d5db;
  border-radius: 3px;
}
.col-content::-webkit-scrollbar-track {
  background: #f9fafb;
}

/* 工具类 */
.ml-3 {
  margin-left: 12px;
}

/* 响应式 */
@media (max-width: 992px) {
  .translator-page {
    padding: 16px;
  }
  .trans-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .result-wrapper {
    flex-direction: column;
  }
  .switch-wrap {
    flex-wrap: wrap;
  }
}
</style>
