// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],

  // 关键：配置 @ 指向 src 目录
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },

  server: {
    hmr: {
      overlay: false // 禁用错误遮罩，同时缓解热更新冲突
    },
    watch: {
      usePolling: true // 强制轮询，避免文件监听异常
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 你的后端服务地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // 移除 /api 前缀
      },
    },
  },
})