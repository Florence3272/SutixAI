<template>
<Header />
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
        <div class="category-info" v-if="currentCategory">
          <span class="category-icon">{{ currentCategory.icon }}</span>
          <h1>{{ currentCategory.categoryName }} 术语数据库</h1>
          <el-tag type="primary" size="small">收录术语:{{ total }} 条</el-tag>
        </div>
      </div>
      <div class="header-right">
        <el-button v-if="userStore.isAdmin" type="primary" @click="openAddDialog">
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
        :data="tableData"
        border
        stripe
        style="width: 100%"
        v-loading="loading"
        :empty-text="'暂无术语数据'"
      >
        <el-table-column
          prop="name"
          label="中文术语"
          width="260"
          sortable
        />
        <el-table-column
          prop="ruName"
          label="俄文术语"
          width="300"
        />
        <el-table-column
          prop="description"
          label="术语定义"
          show-overflow-tooltip
        />
        <el-table-column
          label="操作"
          width="220"
          align="center"
        >
          <template #default="scope">
            <el-button
              type="text"
              size="small"
              @click="goDetail(scope.row)"
            >
              查看
            </el-button>
            <el-button 
              v-if="userStore.isAdmin"
              type="text"
              size="small" 
              @click="openEditDialog(scope.row)"
            >
              编辑
            </el-button>
            <el-button 
              v-if="userStore.isAdmin"
              type="text"
              size="small"
              danger
              @click="handleDelete(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        @current-change="handlePageChange"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 24px; text-align: right"
      />
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
          prop="description"
        >
          <el-input
            v-model="termForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入术语定义(可选)"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showAddDialog = false">取消</el-button>
          <el-button type="primary" @click="submitTermForm" :loading="submitLoading">
            {{ isEditMode ? '保存修改' : '添加' }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import Header from '@/components/Header.vue'

import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { termApi } from '@/api/termApi'
import { termCategoryApi } from '@/api/termCategoryApi'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

const router = useRouter()
const route = useRoute()
const termFormRef = ref(null)

// 状态变量
const currentCategory = ref(null)
const searchText = ref('')
const showAddDialog = ref(false)
const isEditMode = ref(false)
const editId = ref(null)
const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

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
const termForm = reactive({
  name: '',
  ruName: '',
  description: ''
})

// 获取分类信息
const loadCategory = async () => {
  try {
    const categoryId = route.params.categoryId
    const res = await termCategoryApi.getById(categoryId)
    currentCategory.value = res.data
  } catch (error) {
    ElMessage.error('获取分类信息失败:' + error.message)
    await router.push('/home')
  }
}

// 获取术语列表
const loadTerms = async () => {
  loading.value = true
  try {
    const res = await termApi.pageList({
      page: page.value,
      size: size.value,
      keyword: searchText.value.trim(),
      categoryId: route.params.categoryId
    })
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    ElMessage.error('获取术语列表失败:' + error.message)
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  page.value = 1
  loadTerms()
}

// 分页切换
const handlePageChange = (newPage) => {
  page.value = newPage
  loadTerms()
}

// 新增弹窗
const openAddDialog = () => {
  isEditMode.value = false
  editId.value = null
  termForm.name = ''
  termForm.ruName = ''
  termForm.description = ''
  showAddDialog.value = true
}

// 编辑弹窗
const openEditDialog = (row) => {
  isEditMode.value = true
  editId.value = row.id
  termForm.name = row.name
  termForm.ruName = row.ruName
  termForm.description = row.description
  showAddDialog.value = true
}

// 提交表单
const submitTermForm = async () => {
  termFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      const formDTO = {
        name: termForm.name,
        ruName: termForm.ruName,
        description: termForm.description,
        categoryId: Number(route.params.categoryId)
      }

      if (isEditMode.value) {
        formDTO.id = editId.value
        await termApi.update(formDTO)
        ElMessage.success('修改成功')
      } else {
        await termApi.add(formDTO)
        ElMessage.success('添加成功')
      }

      showAddDialog.value = false
      termFormRef.value.resetFields()
      await loadTerms()
    } catch (error) {
      ElMessage.error((isEditMode.value ? '修改' : '添加') + '失败:' + error.message)
    } finally {
      submitLoading.value = false
    }
  })
}

// 删除术语
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条术语吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await termApi.delete(row.id)
    ElMessage.success('删除成功')
    await loadTerms()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败:' + error.message)
    }
  }
}

// 查看详情
const goDetail = (term) => {
  router.push(`/term-detail?termId=${term.id}`)
}

// 跳转到当前分类的学习页面
const goToStudy = () => {
  router.push({
    path: '/term-study',
    query: { category: route.params.categoryId }
  })
}

onMounted(async () => {
  await loadCategory()
  await loadTerms()
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
