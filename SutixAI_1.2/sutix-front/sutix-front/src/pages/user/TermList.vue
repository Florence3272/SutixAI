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
      <el-table-column label="术语解释" prop="description" show-overflow-tooltip min-width="250" />
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
          <el-button type="text" @click="goDetail(scope.row)">查看</el-button>
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
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入术语解释" />
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
import {ref, reactive, computed, onMounted, nextTick} from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { termApi } from '@/api/termApi';
import { termCategoryApi } from '@/api/termCategoryApi';

const router = useRouter()
const route = useRoute()

// 状态变量
const searchText = ref('')
const selectedCategory = ref('')
const page = ref(1)
const size = ref(10)
const total = ref(0) // 新增：分页总数
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref('') // 修改：用ID标识编辑项（替换原editIndex）
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
const categoryList = ref([]) // 修改：从后端获取分类列表

// 术语表单
const form = reactive({
  name: '',
  ruName: '',
  desc: '',
  description: '',
  category: ''
})

// 分数据库表单
const categoryForm = reactive({
  id: '',
  name: '',
  icon: '',
  description: ''
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

const getCategoryList = async () => {
  try {
    const res = await termCategoryApi.listAll()
    const systemCategory = { id: 'favorites', name: '我的喜欢', icon: '❤️', description: '我收藏的术语', isSystem: true }

    // 关键修复：取 res.data，并且把 categoryName 映射成 name
    const realList = res.data.map(item => ({
      id: item.id,
      name: item.categoryName, // 后端是 categoryName → 前端需要 name
      icon: item.icon,
      description: item.categoryName
    }))

    categoryList.value = [systemCategory, ...realList]
  } catch (error) {
    ElMessage.error('获取分类列表失败：' + error.message)
  }
}

// 修改：从后端分页查询术语列表
const getTermPageList = async () => {
  try {
    // 构造查询参数
    const queryDTO = {
      pageNum: page.value,
      pageSize: size.value,
      keyword: searchText.value.trim(),
      categoryId: selectedCategory.value === 'favorites' ? null : selectedCategory.value,
      isLiked: selectedCategory.value === 'favorites'
    }

    const res = await termApi.pageList(queryDTO)
    termList.value = res.data.records || []
    total.value = res.data.total || 0

    tableData.value = [...termList.value]
  } catch (error) {
    ElMessage.error('获取术语列表失败：' + error.message)
  }
}

// 初始化
const initData = async () => {
  await getCategoryList()
  await getTermPageList()
}

// 选择分数据库
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  page.value = 1 // 重置页码
  getTermPageList()
}

// 搜索与筛选
const handleSearch = () => {
  page.value = 1
  getTermPageList()
  selectedRows.value = []
}

// 修改：调用后端接口切换收藏状态
const toggleLike = async (term) => {
  try {
    // 假设后端有切换收藏的接口，如果没有需要新增：这里先模拟，实际需根据后端接口调整
    // await termApi.toggleLike(term.id)
    term.isLiked = !term.isLiked
    ElMessage.success(term.isLiked ? '已添加到我的喜欢' : '已取消收藏')

    // 如果当前在"我的喜欢"页面，刷新列表
    if (selectedCategory.value === 'favorites') {
      getTermPageList()
    }
  } catch (error) {
    ElMessage.error('操作收藏失败：' + error.message)
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

// 修改：调用后端接口批量移动术语
const confirmMove = async () => {
  if (!targetCategory.value) {
    ElMessage.warning('请选择目标分类')
    return
  }

  try {
    await Promise.all(
        movingTerms.value.map(term =>
            termApi.update({
              ...term,
              categoryId: targetCategory.value  // ✅ 修复字段名
            })
        )
    )

    moveDialogVisible.value = false
    ElMessage.success(`成功移动 ${movingTerms.value.length} 条术语`)
    getTermPageList()
  } catch (error) {
    ElMessage.error('移动术语失败：' + error.message)
  }
}

// 修改：调用后端接口批量删除术语
const batchDelete = async () => {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的术语')
    return
  }

  ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 条术语吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      // 批量删除：循环调用删除接口
      await Promise.all(
          selectedRows.value.map(term => termApi.delete(term.id))
      )

      ElMessage.success('批量删除成功')
      getTermPageList()
      selectedRows.value = []
    } catch (error) {
      ElMessage.error('批量删除失败：' + error.message)
    }
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

// 修改：调用后端接口提交分类表单（新增/编辑）
const submitCategoryForm = async () => {
  if (!categoryForm.name || !categoryForm.icon) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    // ✅ 严格按照后端字段名构造参数
    const params = {
      categoryName: categoryForm.name,
      icon: categoryForm.icon,
      description: categoryForm.description
    }

    if (isEditCategory.value) {
      params.id = categoryForm.id
      await termCategoryApi.update(params)
      ElMessage.success('编辑成功')
    } else {
      await termCategoryApi.save(params)
      ElMessage.success('新增成功')
    }

    categoryFormVisible.value = false
    await getCategoryList()
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 修改：调用后端接口删除分类
const deleteCategory = async (category) => {
  if (category.isSystem) {
    ElMessage.warning('系统分类不能删除')
    return
  }

  ElMessageBox.confirm(`确定删除分数据库"${category.name}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await termCategoryApi.remove(category.id)
      ElMessage.success('删除分类成功')

      // 刷新分类列表
      await getCategoryList()

      // 如果当前选中的是被删除的分类，切换到全部
      if (selectedCategory.value === category.id) {
        selectedCategory.value = ''
        getTermPageList()
      }
    } catch (error) {
      ElMessage.error('删除分类失败：' + error.message)
    }
  }).catch(() => {})
}

// 跳转到详情页
const goDetail = (term) => {
  router.push(`/term-detail?termId=${term.id}`)
}

// 打开新增术语弹窗
const openAddDialog = () => {
  isEdit.value = false
  editId.value = ''
  Object.assign(form, { name: '', ruName: '', desc: '', category: '' })
  dialogVisible.value = true
}

// 打开编辑术语弹窗
const openEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    name: row.name,
    ruName: row.ruName,
    desc: row.desc,
    category: row.category
  })
  dialogVisible.value = true
}

// 修改：调用后端接口提交术语表单（新增/编辑）
const handleSubmit = async () => {
  if (!form.name || !form.ruName || !form.category) {
    ElMessage.warning('请填写完整信息')
    return
  }

  try {
    const formDTO = {
      id: editId.value, // 编辑时传ID
      name: form.name,
      ruName: form.ruName,
      desc: form.desc,
      categoryId: form.category
    }

    if (isEdit.value) {
      // 编辑术语
      await termApi.update(formDTO)
      ElMessage.success('编辑术语成功')
    } else {
      // 新增术语
      await termApi.add(formDTO)
      ElMessage.success('新增术语成功')
    }

    dialogVisible.value = false
    getTermPageList() // 刷新列表
  } catch (error) {
    ElMessage.error((isEdit.value ? '编辑' : '新增') + '术语失败：' + error.message)
  }
}

// 修改：调用后端接口删除单条术语
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该术语吗？', '提示', { type: 'warning' })
    await termApi.delete(row.id)
    ElMessage.success('删除成功')
    getTermPageList() // 刷新列表
  } catch (error) {
    if (error !== 'cancel') { // 排除取消弹窗的情况
      ElMessage.error('删除失败：' + error.message)
    }
  }
}

// 分页切换
const handlePageChange = (newPage) => {
  page.value = newPage
  getTermPageList()
}

onMounted(async () => {
  nextTick(() => {
    if (window.twemoji) {
      twemoji.parse(document.querySelector('.category-tags'), {
        folder: 'svg',
        ext: '.svg',
        className: 'twemoji'
      });
    }
  });
  // 接收首页传过来的搜索词
  if (route.query.search) {
    searchText.value = route.query.search
  }

  // 接收分数据库页面传过来的分类ID
  if (route.query.category) {
    selectedCategory.value = route.query.category
  }

  // 初始化数据
  await initData()
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
