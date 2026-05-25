<template>
  <div class="term-detail">
    <div class="back-btn">
      <el-button @click="$router.back()">← 返回术语列表</el-button>
    </div>

    <div class="detail-card" v-if="term">
      <h2 class="title">{{ term.name }}</h2>

      <div class="info-row">
        <label>英文术语：</label>
        <span>{{ term.enName || '无' }}</span>
      </div>

      <div class="info-row">
        <label>俄文术语：</label>
        <span>{{ term.ruName || '无' }}</span>
      </div>

      <div class="info-row">
        <label>所属分类：</label>
        <el-tag type="primary">{{ term.category }}</el-tag>
      </div>

      <div class="info-row">
        <label>创建时间：</label>
        <span>{{ term.createTime }}</span>
      </div>

      <div class="desc-box">
        <label>术语解释：</label>
        <div class="desc-content">{{ term.desc || '暂无解释' }}</div>
      </div>

      <div class="action-btns">
        <el-button type="primary" @click="$router.push('/term-list')">
          返回列表
        </el-button>
        <el-button type="success" @click="goEdit">
          编辑此术语
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const term = ref(null)

const getTermList = () => {
  const data = localStorage.getItem('sutix-term-list')
  return data ? JSON.parse(data) : []
}

onMounted(() => {
  const index = route.query.index
  const list = getTermList()
  if (index >= 0 && list[index]) {
    term.value = list[index]
  }
})

const goEdit = () => {
  router.push({
    path: '/term-list',
    query: { edit: route.query.index }
  })
}
</script>

<style scoped>
.term-detail {
  padding: 20px;
}
.back-btn {
  margin-bottom: 20px;
}
.detail-card {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.title {
  font-size: 26px;
  margin-bottom: 25px;
  color: #333;
}
.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 16px;
}
.info-row label {
  width: 110px;
  font-weight: bold;
  color: #666;
}
.desc-box {
  margin: 25px 0;
}
.desc-box label {
  display: block;
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
}
.desc-content {
  background: #f7f8fa;
  padding: 15px;
  border-radius: 8px;
  line-height: 1.6;
  min-height: 80px;
}
.action-btns {
  margin-top: 30px;
  display: flex;
  gap: 15px;
}
</style>