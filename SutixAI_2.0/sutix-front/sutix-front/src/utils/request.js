import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建实例
const service = axios.create({
    baseURL: '/api',
    timeout: 10000,
})

// 请求拦截器：自动在 header 加 token
service.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = 'Bearer ' + token
        }
        return config
    },
    error => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
    (response) => response.data,
    (error) => {
        const status = error.response?.status
        const msg = error.response?.data?.msg || error.message

        if (status === 401) {
            // 未登录 / Token 过期 → 跳回登录页
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
            window.location.href = '/login'
        } else if (status === 403) {
            // 权限不足
            ElMessage.error(msg || '权限不足，仅管理员可操作')
        } else if (status === 500) {
            ElMessage.error('服务器内部错误：' + msg)
        } else {
            console.error('请求错误', status, msg)
        }

        return Promise.reject(error)
    }
)

export default service
