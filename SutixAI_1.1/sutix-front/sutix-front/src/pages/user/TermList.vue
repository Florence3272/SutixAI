<template>
  <div class="term-list">
    <div class="search-box">
      <el-input
          v-model="searchText"
          placeholder="搜索术语（中文/俄文）"
          style="width: 300px"
          @input="handleSearch"
      />
      <el-select
          v-model="searchCategory"
          placeholder="按分类筛选"
          style="width: 180px; margin-left: 10px"
          @change="handleSearch"
      >
        <el-option label="全部" value="" />
        <el-option
            v-for="item in categoryList"
            :key="item.id"
            :label="item.categoryName"
            :value="item.categoryName"
        />
      </el-select>
      <el-button type="primary" style="margin-left: 10px" @click="openAddDialog">
        新增术语
      </el-button>
    </div>

    <el-table :data="tableData" border style="margin-top: 20px" v-loading="loading">
      <el-table-column label="序号" width="80" type="index" />
      <el-table-column label="中文术语" prop="name" />
      <el-table-column label="俄文术语" prop="ruName" />
      <el-table-column label="术语解释" prop="description" />
      <el-table-column label="分类" prop="categoryName" />
      <el-table-column label="创建时间" prop="createTime" width="180" />
      <el-table-column label="操作" width="220">
        <template #default="scope">
          <el-button type="text" @click="goDetail(scope.row.id)">查看</el-button>
          <el-button type="text" @click="openEditDialog(scope.row)">编辑</el-button>
          <el-button type="text" danger @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; text-align: right"
        @size-change="handleSearch"
        @current-change="handleSearch"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="术语信息" width="600px">
      <el-form :model="form" label-width="80px" :rules="formRules" ref="formRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="中文术语" prop="name">
              <el-input v-model="form.name" placeholder="请输入中文术语" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="俄文术语" prop="ruName">
          <el-input v-model="form.ruName" placeholder="请输入俄文术语" />
        </el-form-item>
        <el-form-item label="术语解释" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入术语解释" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
                v-for="item in categoryList"
                :key="item.id"
                :label="item.categoryName"
                :value="item.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

// 请求实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' }
})

// 请求拦截器（自动带token）
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// 响应拦截器
request.interceptors.response.use(res => res.data, err => {
  ElMessage.error(err.response?.data?.msg || '请求失败')
  return Promise.reject(err)
})

const router = useRouter()
const route = useRoute()

// 搜索
const searchText = ref('')
const searchCategory = ref('')

// 分页
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 状态
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 分类列表
const categoryList = ref([])

// 表格数据
const tableData = ref([])

// 表单
const form = reactive({
  id: '',
  name: '',
  ruName: '',
  description: '',
  categoryId: ''
})

// 校验规则
const formRules = {
  name: [{ required: true, message: '请输入中文术语', trigger: 'blur' }],
  ruName: [{ required: true, message: '请输入俄文术语', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}


// 1. 获取分类（数据库）
const getCategoryList = async () => {
  try {
    const res = await request.get('/term/category/list')
    if (res.code === 200) categoryList.value = res.data
  } catch (e) {}
}


// 2. 查询术语列表（分页+搜索）
const getTermList = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value,
      keyword: searchText.value,
      categoryName: searchCategory.value
    }
    const res = await request.get('/term/list', { params })
    if (res.code === 200) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}


// 3. 搜索
const handleSearch = () => {
  page.value = 1
  getTermList()
}

//4.查看详情
const goDetail = (id) => {
  router.push(`/term/detail?id=${id}`)
}


// 5. 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: '', name: '',  ruName: '', description: '', categoryId: '' })
  dialogVisible.value = true
}

// 6. 打开编辑弹窗
const openEditDialog = async (row) => {
  isEdit.value = true
  try {
    const res = await request.get(`/term/detail/${row.id}`)
    if (res.code === 200) Object.assign(form, res.data)
  } catch (e) {}
  dialogVisible.value = true
}


// 7. 提交：新增 / 编辑
const handleSubmit = async () => {
  await formRef.value.validate()
  try {
    if (isEdit.value) {
      await request.put('/term/update', form)
      ElMessage.success('编辑成功')
    } else {
      await request.post('/term/add', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getTermList()
  } catch (e) {}
}


// 8. 删除术语
const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除？', '提示')
  await request.delete(`/term/delete/${id}`)
  ElMessage.success('删除成功')
  getTermList()
}

// 初始化
onMounted(async () => {
  if (route.query.keyword) searchText.value = route.query.keyword
  await getCategoryList()
  getTermList()
})
</script>

<style scoped>
.term-list {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
.search-box {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}
</style>