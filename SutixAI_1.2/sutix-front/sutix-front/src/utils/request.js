import axios from 'axios';

// 1. 创建实例
const service = axios.create({
    baseURL: '/api', // 你的接口前缀
    timeout: 10000,
});

// 请求拦截器：自动在 header 加 token
service.interceptors.request.use(
    config => {
        // 从本地取出 token
        const token = localStorage.getItem('token')

        // 有 token 就自动带上
        if (token) {
            config.headers.Authorization = 'Bearer ' + token
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 3. 响应拦截器（可选，处理 401/403）
service.interceptors.response.use(
    (response) => response.data,
    (error) => {
        console.error('请求错误', error);
        return Promise.reject(error);
    }
);

export default service;