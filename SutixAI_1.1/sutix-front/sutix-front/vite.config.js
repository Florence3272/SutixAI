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
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 你的后端服务地址
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // 移除 /api 前缀
      },
    },
  },
})