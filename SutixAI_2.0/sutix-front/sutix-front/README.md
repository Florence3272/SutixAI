my-term-app/         项目根目录（所有命令必须在这里执行）
├── src/              核心源码（所有页面、组件、配置都在这里）
│   ├── App.vue       项目根组件（所有页面的入口）
│   ├── main.js       项目入口文件（加载插件、启动项目）
│   ├── router/       路由（页面跳转管理）
│   │   └── index.js
│   ├── pages/        所有页面
│   │   └── user/     用户端页面
│   │       ├── Home.vue      首页
│   │       └── Login.vue     登录页
│   ├── layouts/      布局组件
│   │   ├── UserLayout.vue    用户端公共布局（导航栏等）
│   │   └── AdminLayout.vue   管理端公共布局
│   └── store/        状态管理（用户信息、全局配置）
│       └── user.js
├── package.json      项目配置文件
└── vite.config.js    Vite 配置

cd ~/Desktop/my-term-app
npm run dev


# 安装依赖（第一次 / 新增插件时用）
npm install
npm install vue-router@4 element-plus pinia

# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the [Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).
