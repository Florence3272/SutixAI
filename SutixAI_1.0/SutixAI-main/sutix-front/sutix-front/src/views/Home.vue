<template>
  <div class="home-page">
    <!-- 顶部搜索栏 -->
    <div class="search-box">
      <el-input
          v-model="keyword"
          placeholder="请输入术语名称"
          clearable
          @keyup.enter="getList"
      >
        <template #append>
          <el-button @click="getList">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 分类 -->
    <div class="category-box">
      <el-button
          :type="activeCid === 0 ? 'primary' : 'default'"
          @click="selectCategory(0)"
      >
        全部
      </el-button>
      <el-button
          v-for="item in categoryList"
          :key="item.id"
          :type="activeCid === item.id ? 'primary' : 'default'"
          @click="selectCategory(item.id)"
      >
        {{ item.categoryName }}
      </el-button>
    </div>

    <!-- 术语列表 -->
    <div class="term-list">
      <div
          class="term-item"
          v-for="item in termList"
          :key="item.id"
          @click="goDetail(item.id)"
      >
        <h3>{{ item.termName }}</h3>
        <p class="explain">{{ item.termExplain }}</p>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-box">
      <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          total="total"
          layout="total, prev, pager, next"
          @size-change="getList"
          @current-change="getList"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()
const keyword = ref('')
const categoryList = ref([])
const termList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const activeCid = ref(0)

// 获取分类
const getCategory = async () => {
  const res = await request.get('/category/list')
  categoryList.value = res.data.data
}

// 获取术语列表
const getList = async () => {
  const res = await request.get('/term/list', {
    params: {
      keyword: keyword.value,
      categoryId: activeCid.value,
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
  })
  termList.value = res.data.data.records
  total.value = res.data.data.total
}

// 选择分类
const selectCategory = (id) => {
  activeCid.value = id
  pageNum.value = 1
  getList()
}

// 进入详情
const goDetail = (id) => {
  router.push(`/detail/${id}`)
}

onMounted(() => {
  getCategory()
  getList()
})
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}
.search-box {
  margin-bottom: 20px;
}
.category-box {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}
.term-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}
.term-item {
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 8px;
  cursor: pointer;
}
.term-item h3 {
  font-size: 18px;
  margin-bottom: 10px;
}
.pagination-box {
  margin-top: 30px;
  text-align: center;
}
</style>