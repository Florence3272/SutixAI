<template>
  <div class="term-list">
    <!-- 顶部搜索与操作区 -->
    <div class="search-bar">
      <el-input
        v-model="searchText"
        placeholder="搜索术语（中文/俄文）"
        style="width: 360px"
        @input="handleSearch"
        clearable
        prefix-icon="el-icon-search"
      />
      <div class="action-buttons">
        <el-button @click="openCategoryManageDialog">
          <i class="el-icon-setting"></i> 管理分数据库
        </el-button>
        <el-button type="primary" @click="openAddDialog">
          <i class="el-icon-plus"></i> 新增术语
        </el-button>
      </div>
    </div>

    <!-- 分数据库筛选区 -->
    <div class="category-filter-section">
      <div class="filter-header">
        <span class="filter-title">📂 专业分数据库</span>
        <span class="filter-tip">点击标签筛选对应数据库内容</span>
      </div>
      <div class="category-tags">
        <el-tag
          :type="selectedCategory === '' ? 'primary' : 'default'"
          size="large"
          class="category-tag"
          @click="selectCategory('')"
          effect="dark"
        >
          全部数据库
        </el-tag>
        <el-tag
          v-for="cat in categoryList"
          :key="cat.id"
          :type="selectedCategory === cat.id ? 'primary' : 'default'"
          size="large"
          class="category-tag"
          :class="{ 'favorite-tag': cat.id === 'favorites' }"
          @click="selectCategory(cat.id)"
          effect="dark"
        >
          {{ cat.icon }} {{ cat.name }}
          <span v-if="cat.id !== 'favorites'" class="tag-count">({{ getCategoryTermCount(cat.id) }})</span>
          <span v-else class="tag-count">({{ favoriteCount }})</span>
        </el-tag>
      </div>
    </div>

    <!-- 术语表格 -->
    <el-table :data="tableData" border stripe style="margin-top: 24px" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" width="80" type="index" align="center" />
      <el-table-column label="中文术语" prop="name" min-width="150" />
      <el-table-column label="俄文术语" prop="ruName" min-width="200" />
      <el-table-column label="术语解释" prop="desc" show-overflow-tooltip min-width="250" />
      <el-table-column label="所属数据库" prop="categoryName" width="140" align="center" />
      <el-table-column label="创建时间" prop="createTime" width="180" align="center" />
      <el-table-column label="操作" width="320" align="center">
        <template #default="scope">
          <el-button 
            type="text" 
            :icon="scope.row.isLiked ? 'el-icon-star-on' : 'el-icon-star-off'"
            :style="{ color: scope.row.isLiked ? '#f7ba2a' : '#909399' }"
            @click="toggleLike(scope.row)"
            title="收藏/取消收藏"
          >
            {{ scope.row.isLiked ? '已收藏' : '收藏' }}
          </el-button>
          <el-button type="text" @click="openMoveDialog([scope.row])">移动</el-button>
          <el-button type="text" @click="goDetail(scope.$index)">查看</el-button>
          <el-button type="text" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="text" danger @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 批量操作栏 -->
    <div v-if="selectedRows.length > 0" class="batch-action-bar">
      <span>已选中 {{ selectedRows.length }} 条术语</span>
      <el-button type="primary" size="small" @click="openMoveDialog(selectedRows)">批量移动</el-button>
      <el-button type="danger" size="small" @click="batchDelete">批量删除</el-button>
    </div>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="tableData.length"
      layout="total, sizes, prev, pager, next, jumper"
      style="margin-top: 24px; text-align: right"
    />

    <!-- 新增/编辑术语弹窗 -->
    <el-dialog v-model="dialogVisible" title="术语信息" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="中文术语" required>
          <el-input v-model="form.name" placeholder="请输入中文术语" />
        </el-form-item>
        <el-form-item label="俄文术语" required>
          <el-input v-model="form.ruName" placeholder="请输入俄文术语" />
        </el-form-item>
        <el-form-item label="术语解释">
          <el-input v-model="form.desc" type="textarea" rows="3" placeholder="请输入术语解释" />
        </el-form-item>
        <el-form-item label="所属数据库" required>
          <el-select v-model="form.category" placeholder="请选择所属数据库" style="width: 100%">
            <el-option 
              v-for="cat in editableCategories" 
              :key="cat.id"
              :label="cat.name" 
              :value="cat.id" 
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分数据库管理弹窗 -->
    <el-dialog v-model="categoryManageVisible" title="管理分数据库" width="700px">
      <div class="category-manage-header">
        <el-button type="primary" size="small" @click="openAddCategoryDialog">
          <i class="el-icon-plus"></i> 新增分数据库
        </el-button>
      </div>
      
      <el-table :data="editableCategories" border stripe style="margin-top: 16px">
        <el-table-column label="图标" width="80" align="center">
          <template #default="scope">
            <span style="font-size: 20px">{{ scope.row.icon }}</span>
          </template>
        </el-table-column>
        <el-table-column label="数据库名称" prop="name" />
        <el-table-column label="术语数量" width="100" align="center">
          <template #default="scope">
            {{ getCategoryTermCount(scope.row.id) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <el-button type="text" size="small" @click="openEditCategoryDialog(scope.row)">编辑</el-button>
            <el-button 
              type="text" 
              size="small" 
              danger 
              @click="deleteCategory(scope.row)"
              :disabled="getCategoryTermCount(scope.row.id) > 0"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="tip-text" style="margin-top: 12px; color: #909399; font-size: 13px">
        💡 提示：只有空数据库才能被删除，有术语的数据库请先移动或删除所有术语
      </div>
    </el-dialog>

    <!-- 新增/编辑分数据库弹窗 -->
    <el-dialog v-model="categoryFormVisible" :title="isEditCategory ? '编辑分数据库' : '新增分数据库'" width="500px">
      <el-form :model="categoryForm" label-width="100px">
        <el-form-item label="数据库名称" required>
          <el-input v-model="categoryForm.name" placeholder="请输入数据库名称" />
        </el-form-item>
        <el-form-item label="图标" required>
          <el-input v-model="categoryForm.icon" placeholder="请输入emoji图标，如：⚙️" maxlength="2" />
          <div style="margin-top: 8px; font-size: 13px; color: #909399">
            可以从 <a href="https://emojipedia.org/" target="_blank">Emoji百科</a> 复制图标
          </div>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="categoryForm.description" type="textarea" rows="2" placeholder="请输入数据库描述（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryFormVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCategoryForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 移动术语弹窗 -->
    <el-dialog v-model="moveDialogVisible" title="移动术语" width="500px">
      <p>将选中的 <strong>{{ movingTerms.length }}</strong> 条术语移动到：</p>
      <el-select v-model="targetCategory" placeholder="请选择目标数据库" style="width: 100%; margin: 16px 0">
        <el-option 
          v-for="cat in editableCategories" 
          :key="cat.id"
          :label="cat.name" 
          :value="cat.id" 
        />
      </el-select>
      <template #footer>
        <el-button @click="moveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmMove" :disabled="!targetCategory">确定移动</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 状态变量
const searchText = ref('')
const selectedCategory = ref('')
const page = ref(1)
const size = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editIndex = ref(-1)
const selectedRows = ref([])

// 分数据库管理相关
const categoryManageVisible = ref(false)
const categoryFormVisible = ref(false)
const isEditCategory = ref(false)
const moveDialogVisible = ref(false)
const movingTerms = ref([])
const targetCategory = ref('')

const termList = ref([])
const tableData = ref([])

// 术语表单
const form = reactive({
  name: '',
  ruName: '',
  desc: '',
  category: ''
})

// 分数据库表单
const categoryForm = reactive({
  id: '',
  name: '',
  icon: '',
  description: ''
})

// 初始化默认分数据库（包含特殊的"我的喜欢"数据库）
const defaultCategories = [
  { id: 'favorites', name: '我的喜欢', icon: '❤️', description: '我收藏的术语', isSystem: true },
  { id: 'mechanical', name: '机械工程', icon: '⚙️', description: '机械设计、制造、自动化相关术语' },
  { id: 'electrical', name: '电气工程', icon: '⚡', description: '电力系统、电子技术、自动化控制' },
  { id: 'chemical', name: '化学化工', icon: '🧪', description: '有机化学、无机化学、化工工艺' },
  { id: 'medical', name: '医药卫生', icon: '💊', description: '临床医学、药学、生物医学' },
  { id: 'computer', name: '计算机科学', icon: '💻', description: '软件开发、人工智能、网络技术' },
  { id: 'construction', name: '建筑工程', icon: '🏗️', description: '建筑设计、结构工程、施工技术' },
  { id: 'transport', name: '交通运输', icon: '🚄', description: '铁路、公路、航空、水运术语' },
  { id: 'energy', name: '能源动力', icon: '🔋', description: '石油、天然气、新能源技术' },
  { id: 'legal', name: '法律法务', icon: '⚖️', description: '中俄法律、合同、商务法务' },
  { id: 'economic', name: '经济贸易', icon: '💰', description: '国际贸易、金融、市场营销' },
  { id: 'agricultural', name: '农业科学', icon: '🌾', description: '农学、畜牧、林业、渔业' },
  { id: 'environmental', name: '环境科学', icon: '🌍', description: '环境保护、生态工程、污染治理' }
]

// 计算属性：所有分数据库列表（从localStorage读取）
const categoryList = computed(() => {
  const saved = localStorage.getItem('sutix-category-list')
  if (saved) {
    return JSON.parse(saved)
  }
  // 首次使用时保存默认分类
  localStorage.setItem('sutix-category-list', JSON.stringify(defaultCategories))
  return defaultCategories
})

// 计算属性：可编辑的分类列表（排除系统分类"我的喜欢"）
const editableCategories = computed(() => {
  return categoryList.value.filter(cat => !cat.isSystem)
})

// 计算属性：分类ID到名称的映射
const categoryMap = computed(() => {
  const map = {}
  categoryList.value.forEach(cat => {
    map[cat.id] = cat.name
  })
  return map
})

// 计算属性：收藏术语数量
const favoriteCount = computed(() => {
  return termList.value.filter(term => term.isLiked).length
})

// 方法：获取指定分类的术语数量
const getCategoryTermCount = (categoryId) => {
  return termList.value.filter(term => term.category === categoryId).length
}

// 初始化术语列表
const initTermList = () => {
  const saved = localStorage.getItem('sutix-term-list')
  if (saved) {
    termList.value = JSON.parse(saved)
  } else {
    // 初始化示例数据
    termList.value = [
      {
        name: "国际贸易",
        ruName: "Международная торговля",
        desc: "跨越国境的商品与服务交换活动，是经济全球化重要组成部分。",
        category: "economic",
        createTime: "2026-05-15",
        isLiked: false
      },
      {
        name: "人工智能",
        ruName: "Искусственный интеллект",
        desc: "使机器模拟人类感知、推理、学习与决策的技术科学。",
        category: "computer",
        createTime: "2026-05-15",
        isLiked: true
      },
      {
        name: "生态环境",
        ruName: "Экологическая среда",
        desc: "生物群落与周边自然因素相互作用形成的整体生态系统。",
        category: "environmental",
        createTime: "2026-05-15",
        isLiked: false
      },
      {
        name: "智能制造",
        ruName: "Умное производство",
        desc: "依托物联网、大数据与自动化实现工厂智能化生产模式。",
        category: "mechanical",
        createTime: "2026-05-15",
        isLiked: false
      }
    ]
    localStorage.setItem('sutix-term-list', JSON.stringify(termList.value))
  }
  
  // 为每条数据添加categoryName字段
  termList.value.forEach(term => {
    term.categoryName = categoryMap.value[term.category] || '未分类'
  })
  
  tableData.value = [...termList.value]
}

// 保存分数据库列表
const saveCategoryList = () => {
  localStorage.setItem('sutix-category-list', JSON.stringify(categoryList.value))
}

// 保存术语列表
const saveTermList = () => {
  localStorage.setItem('sutix-term-list', JSON.stringify(termList.value))
}

// 选择分数据库
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  handleSearch()
}

// 搜索与筛选
const handleSearch = () => {
  let result = [...termList.value]
  
  // 关键词搜索
  if (searchText.value.trim()) {
    const keyword = searchText.value.trim().toLowerCase()
    result = result.filter(item =>
      item.name.toLowerCase().includes(keyword) ||
      item.ruName.toLowerCase().includes(keyword) ||
      item.desc.toLowerCase().includes(keyword)
    )
  }
  
  // 分数据库筛选
  if (selectedCategory.value) {
    if (selectedCategory.value === 'favorites') {
      // 筛选收藏的术语
      result = result.filter(item => item.isLiked)
    } else {
      // 筛选普通分类
      result = result.filter(item => item.category === selectedCategory.value)
    }
  }
  
  tableData.value = result
  page.value = 1
  selectedRows.value = []
}

// 切换术语收藏状态
const toggleLike = (term) => {
  term.isLiked = !term.isLiked
  saveTermList()
  ElMessage.success(term.isLiked ? '已添加到我的喜欢' : '已取消收藏')
  
  // 如果当前在"我的喜欢"页面，取消收藏后自动刷新
  if (selectedCategory.value === 'favorites' && !term.isLiked) {
    handleSearch()
  }
}

// 表格多选
const handleSelectionChange = (val) => {
  selectedRows.value = val
}

// 打开移动术语弹窗
const openMoveDialog = (terms) => {
  movingTerms.value = terms
  targetCategory.value = ''
  moveDialogVisible.value = true
}

// 确认移动术语
const confirmMove = () => {
  movingTerms.value.forEach(term => {
    term.category = targetCategory.value
    term.categoryName = categoryMap.value[targetCategory.value]
  })
  
  saveTermList()
  moveDialogVisible.value = false
  ElMessage.success(`成功移动 ${movingTerms.value.length} 条术语`)
  handleSearch()
}

// 批量删除术语
const batchDelete = () => {
  ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条术语吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    selectedRows.value.forEach(row => {
      const index = termList.value.findIndex(t => t.name === row.name)
      if (index > -1) {
        termList.value.splice(index, 1)
      }
    })
    
    saveTermList()
    ElMessage.success('批量删除成功')
    handleSearch()
  }).catch(() => {})
}

// 打开分数据库管理弹窗
const openCategoryManageDialog = () => {
  categoryManageVisible.value = true
}

// 打开新增分数据库弹窗
const openAddCategoryDialog = () => {
  isEditCategory.value = false
  Object.assign(categoryForm, { id: '', name: '', icon: '', description: '' })
  categoryFormVisible.value = true
}

// 打开编辑分数据库弹窗
const openEditCategoryDialog = (category) => {
  isEditCategory.value = true
  Object.assign(categoryForm, category)
  categoryFormVisible.value = true
}

// 提交分数据库表单
const submitCategoryForm = () => {
  if (!categoryForm.name || !categoryForm.icon) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  if (isEditCategory.value) {
    // 编辑模式
    const index = categoryList.value.findIndex(cat => cat.id === categoryForm.id)
    if (index > -1) {
      categoryList.value[index] = { ...categoryForm.value }
      
      // 更新所有术语的categoryName
      termList.value.forEach(term => {
        if (term.category === categoryForm.id) {
          term.categoryName = categoryForm.name
        }
      })
      
      saveTermList()
      ElMessage.success('编辑成功')
    }
  } else {
    // 新增模式：生成唯一ID
    const newCategory = {
      id: 'cat_' + Date.now(),
      name: categoryForm.name,
      icon: categoryForm.icon,
      description: categoryForm.description
    }
    categoryList.value.push(newCategory)
    ElMessage.success('新增成功')
  }
  
  saveCategoryList()
  categoryFormVisible.value = false
}

// 删除分数据库
const deleteCategory = (category) => {
  ElMessageBox.confirm(`确定删除分数据库"${category.name}"吗？`, '提示', {
    type: 'warning'
  }).then(() => {
    const index = categoryList.value.findIndex(cat => cat.id === category.id)
    if (index > -1) {
      categoryList.value.splice(index, 1)
      saveCategoryList()
      ElMessage.success('删除成功')
      
      // 如果当前选中的是被删除的分类，切换到全部
      if (selectedCategory.value === category.id) {
        selectedCategory.value = ''
        handleSearch()
      }
    }
  }).catch(() => {})
}

// 以下是原有功能方法（略作修改）
const goDetail = (index) => {
  router.push('/term-detail?index=' + index)
}

const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { name: '', ruName: '', desc: '', category: '' })
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  editIndex.value = tableData.value.indexOf(row)
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = () => {
  if (!form.name || !form.ruName || !form.category) {
    ElMessage.warning('请填写完整信息')
    return
  }
  
  const newTerm = {
    ...form,
    categoryName: categoryMap.value[form.category],
    createTime: new Date().toISOString().slice(0, 10),
    isLiked: false
  }
  
  if (isEdit.value) {
    tableData.value[editIndex.value] = { ...newTerm }
    const originalIndex = termList.value.findIndex(t => t.name === tableData.value[editIndex.value].name)
    if (originalIndex > -1) {
      termList.value[originalIndex] = { ...newTerm }
    }
    ElMessage.success('编辑成功')
  } else {
    tableData.value.unshift({ ...newTerm })
    termList.value.unshift({ ...newTerm })
    ElMessage.success('新增成功')
  }
  
  saveTermList()
  dialogVisible.value = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该术语吗？', '提示', { type: 'warning' })
    const idx = tableData.value.indexOf(row)
    tableData.value.splice(idx, 1)
    
    const originalIndex = termList.value.findIndex(t => t.name === row.name)
    if (originalIndex > -1) {
      termList.value.splice(originalIndex, 1)
    }
    
    saveTermList()
    ElMessage.success('删除成功')
  } catch {}
}

onMounted(() => {
  initTermList()
  
  // 接收首页传过来的搜索词
  if (route.query.search) {
    searchText.value = route.query.search
    handleSearch()
  }
  
  // 接收分数据库页面传过来的分类ID
  if (route.query.category) {
    selectedCategory.value = route.query.category
    handleSearch()
  }
})
</script>

<style scoped>
.term-list {
  background: #f5f7fa;
  padding: 24px;
  min-height: calc(100vh - 120px);
}

/* 顶部搜索栏 */
.search-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

/* 分数据库筛选区 */
.category-filter-section {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
  padding: 20px 24px;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f2f5;
}

.filter-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.filter-tip {
  font-size: 13px;
  color: #909399;
}

.category-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-tag {
  font-size: 14px;
  padding: 8px 16px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.tag-count {
  margin-left: 4px;
  font-size: 12px;
  opacity: 0.8;
}

.favorite-tag {
  border: 1px solid #ff6b6b !important;
}

.favorite-tag.el-tag--primary {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8787 100%) !important;
}

/* 未选中状态 */
.category-tag:not(.el-tag--primary) {
  background: #f5f7fa;
  color: #606266;
}

.category-tag:not(.el-tag--primary):hover {
  background: #ecf5ff;
  color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.15);
}

/* 选中状态 */
.category-tag.el-tag--primary {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  box-shadow: 0 4px 12px rgba(64,158,255,0.3);
  transform: translateY(-2px);
}

/* 批量操作栏 */
.batch-action-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  background: #fff;
  border-radius: 8px;
  margin-top: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}

/* 术语表格 */
.el-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.el-table th {
  background: #f5f7fa !important;
  font-weight: 600;
}

/* 弹窗样式 */
.category-manage-header {
  display: flex;
  justify-content: flex-end;
}

.tip-text {
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .search-bar {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .search-bar .el-input {
    width: 100% !important;
  }
  
  .action-buttons {
    width: 100%;
    justify-content: flex-end;
  }
  
  .filter-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .term-list {
    padding: 16px;
  }
  
  .category-tags {
    gap: 8px;
  }
  
  .category-tag {
    font-size: 13px;
    padding: 6px 12px;
  }
}
</style>
