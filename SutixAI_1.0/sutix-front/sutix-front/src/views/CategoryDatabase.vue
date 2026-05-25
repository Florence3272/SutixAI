<template>
  <div class="category-database">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-left">
        <el-button 
          icon="el-icon-arrow-left" 
          @click="$router.push('/home')"
          class="back-btn"
        >
          返回首页
        </el-button>
        <div class="category-info">
          <span class="category-icon">{{ currentCategory.icon }}</span>
          <h1>{{ currentCategory.name }} 术语数据库</h1>
          <el-tag type="primary" size="small">收录术语：{{ categoryTerms.length }} 条</el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showAddDialog = true">
          <i class="el-icon-plus"></i> 添加术语
        </el-button>
        <el-button @click="goToStudy">
          <i class="el-icon-reading"></i> 开始学习
        </el-button>
      </div>
    </div>

    <!-- 搜索与筛选区 -->
    <div class="search-section">
      <el-input
        v-model="searchText"
        placeholder="搜索本数据库中的中文/俄文术语"
        style="width: 400px"
        @keyup.enter="handleSearch"
        clearable
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 术语列表 -->
    <div class="term-list-section">
      <el-table
        :data="filteredTerms"
        border
        stripe
        style="width: 100%"
        :empty-text="'暂无术语数据'"
      >
        <el-table-column
          prop="name"
          label="中文术语"
          width="300"
          sortable
        />
        <el-table-column
          prop="ruName"
          label="俄文术语"
          width="350"
        />
        <el-table-column
          prop="definition"
          label="术语定义"
          show-overflow-tooltip
        />
        <el-table-column
          label="操作"
          width="200"
          align="center"
        >
          <template #default="scope">
            <el-button 
              type="primary" 
              size="small" 
              @click="editTerm(scope.row, scope.$index)"
            >
              编辑
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteTerm(scope.$index)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑术语对话框 -->
    <el-dialog
      :title="isEditMode ? '编辑术语' : '添加术语'"
      v-model="showAddDialog"
      width="600px"
    >
      <el-form
        :model="termForm"
        label-width="100px"
        ref="termFormRef"
        :rules="formRules"
      >
        <el-form-item
          label="中文术语"
          prop="name"
        >
          <el-input v-model="termForm.name" placeholder="请输入中文术语" />
        </el-form-item>
        <el-form-item
          label="俄文术语"
          prop="ruName"
        >
          <el-input v-model="termForm.ruName" placeholder="请输入俄文术语" />
        </el-form-item>
        <el-form-item
          label="术语定义"
          prop="definition"
        >
          <el-input 
            v-model="termForm.definition" 
            type="textarea" 
            :rows="3"
            placeholder="请输入术语定义（可选）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitTermForm">
            {{ isEditMode ? '保存修改' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
// 移除了TypeScript类型注解
const termFormRef = ref(null)

// 分类数据库列表（与首页保持一致）
const categoryList = [
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

// 状态变量
const searchText = ref('')
const showAddDialog = ref(false)
const isEditMode = ref(false)
const editIndex = ref(-1)
const allTerms = ref([])

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入中文术语', trigger: 'blur' }
  ],
  ruName: [
    { required: true, message: '请输入俄文术语', trigger: 'blur' }
  ]
}

// 表单数据
const termForm = ref({
  name: '',
  ruName: '',
  definition: ''
})

// 计算属性：当前分类信息
const currentCategory = computed(() => {
  return categoryList.find(cat => cat.id === route.params.categoryId) || {
    id: 'unknown',
    name: '未知分类',
    icon: '❓',
    description: '该分类不存在'
  }
})

// 计算属性：当前分类下的所有术语（数据完全隔离）
const categoryTerms = computed(() => {
  return allTerms.value.filter(term => term.category === route.params.categoryId)
})

// 计算属性：搜索过滤后的术语
const filteredTerms = computed(() => {
  if (!searchText.value.trim()) {
    return categoryTerms.value
  }
  const keyword = searchText.value.trim().toLowerCase()
  return categoryTerms.value.filter(term => 
    term.name.toLowerCase().includes(keyword) || 
    term.ruName.toLowerCase().includes(keyword)
  )
})

// 加载所有术语数据
const loadAllTerms = () => {
  try {
    const data = localStorage.getItem('sutix-term-list')
    if (data) {
      allTerms.value = JSON.parse(data)
    }
  } catch (error) {
    console.error('加载术语数据失败:', error)
    ElMessage.error('加载术语数据失败，请刷新页面重试')
  }
}

// 保存所有术语数据
const saveAllTerms = () => {
  try {
    localStorage.setItem('sutix-term-list', JSON.stringify(allTerms.value))
  } catch (error) {
    console.error('保存术语数据失败:', error)
    ElMessage.error('保存术语数据失败，请重试')
  }
}

// 搜索处理
const handleSearch = () => {
  // 搜索逻辑已在computed中实现
}

// 编辑术语
const editTerm = (row, index) => {
  isEditMode.value = true
  // 找到该术语在总列表中的索引
  editIndex.value = allTerms.value.indexOf(row)
  termForm.value = { ...row }
  showAddDialog.value = true
}

// 删除术语
const deleteTerm = (index) => {
  ElMessage.confirm('确定要删除这条术语吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 找到该术语在总列表中的索引并删除
    const termToDelete = categoryTerms.value[index]
    const totalIndex = allTerms.value.indexOf(termToDelete)
    if (totalIndex > -1) {
      allTerms.value.splice(totalIndex, 1)
      saveAllTerms()
      ElMessage.success('删除成功')
    }
  }).catch(() => {
    // 用户取消删除
  })
}

// 提交表单
const submitTermForm = () => {
  termFormRef.value.validate((valid) => {
    if (valid) {
      if (isEditMode.value) {
        // 编辑模式
        allTerms.value[editIndex.value] = {
          ...allTerms.value[editIndex.value],
          ...termForm.value
        }
        ElMessage.success('修改成功')
      } else {
        // 添加模式：自动添加当前分类标识
        const newTerm = {
          ...termForm.value,
          category: route.params.categoryId
        }
        allTerms.value.push(newTerm)
        ElMessage.success('添加成功')
      }
      
      saveAllTerms()
      showAddDialog.value = false
      // 重置表单
      termFormRef.value.resetFields()
    }
  })
}

// 跳转到当前分类的学习页面
const goToStudy = () => {
  router.push({
    path: '/term-study',
    query: { category: route.params.categoryId }
  })
}

onMounted(() => {
  loadAllTerms()
  
  // 检查分类是否存在
  if (currentCategory.value.id === 'unknown') {
    ElMessage.error('该分类数据库不存在')
    router.push('/home')
  }
})
</script>

<style scoped>
.category-database {
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.back-btn {
  margin-right: 10px;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-icon {
  font-size: 32px;
}

.category-info h1 {
  font-size: 24px;
  margin: 0;
  color: #333;
}

.search-section {
  margin-bottom: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}

.term-list-section {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
  padding: 20px;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 20px;
    align-items: flex-start;
  }
  
  .header-left {
    flex-wrap: wrap;
  }
  
  .search-section .el-input {
    width: 100% !important;
  }
}
</style>
