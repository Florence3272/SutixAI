<template>
  <div class="layout">
    <el-header class="header">
      <div class="logo">📚 Sutix 术语AI平台</div>
      <div class="nav">
        <el-button link @click="$router.push('/home')">首页</el-button>
        <el-button link @click="$router.push('/term-list')">术语库</el-button>
        <el-button link @click="$router.push('/term-study')">术语学习</el-button>
        <el-button link @click="$router.push('/corpus-compare')">语料库</el-button>
        <el-button link @click="$router.push('/translator')">翻译器</el-button>
      </div>
    </el-header>

    <el-main class="main">
      <router-view />
    </el-main>

    <el-footer class="footer">
      © 2026 Sutix 术语AI平台（中-俄）
    </el-footer>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  padding: 0 20px;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.main {
  flex: 1;
  background: #f5f7fa;
  padding: 20px;
}

.footer {
  text-align: center;
  color: #999;
  font-size: 14px;
}
</style>
