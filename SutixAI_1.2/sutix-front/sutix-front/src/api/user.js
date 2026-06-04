import request from '@/utils/request';

/**
 * 用户登录
 * @param {Object} params {username, password}
 * @returns Promise
 */
export const userLogin = async (params) => {
    const res = await request.post('/user/login', params)
    localStorage.setItem('token', res.data.token)
    return res
};

/**
 * 校验用户名是否存在
 * @param {String} username 用户名
 * @returns Promise
 */
export const checkUsername = (username) => {
    return request.get('/user/checkusername', {
        params: { username }
    });
};

/**
 * 用户注册
 * @param {Object} params {username, password, confirmPassword, nickname, phone}
 * @returns Promise
 */
export const userRegister = (params) => {
    return request.post('/user/register', params);
};

/**
 * 获取当前登录用户信息
 * @returns Promise
 */
export const getUserInfo = () => {
    return request.get('/user/info');
};