import request from '@/utils/request';

/**
 * 学习模块 API
 */
export const studyApi = {

    // ============== 学习进度 ==============

    /** 标记术语学习状态 */
    markStatus: (termId, status) => {
        return request({
            url: '/study/progress/mark',
            method: 'post',
            params: { termId, status }
        });
    },

    /** 获取用户指定术语的学习状态 */
    getProgress: (termId) => {
        return request({
            url: `/study/progress/${termId}`,
            method: 'get'
        });
    },

    /** 获取用户某个状态的学习进度列表 */
    listProgress: (status) => {
        return request({
            url: '/study/progress/list',
            method: 'get',
            params: { status }
        });
    },

    // ============== 错题 ==============

    /** 添加错题 */
    addWrong: (termId) => {
        return request({
            url: `/study/wrong/${termId}`,
            method: 'post'
        });
    },

    /** 获取用户错题列表（含术语详情） */
    listWrong: () => {
        return request({
            url: '/study/wrong/list',
            method: 'get'
        });
    },

    /** 清空错题 */
    clearWrong: () => {
        return request({
            url: '/study/wrong/clear',
            method: 'delete'
        });
    },

    /** 移除单条错题 */
    removeWrong: (termId) => {
        return request({
            url: `/study/wrong/${termId}`,
            method: 'delete'
        });
    },

    // ============== 视频 ==============

    /** 获取视频列表 */
    listVideo: (categoryId) => {
        return request({
            url: '/study/video/list',
            method: 'get',
            params: { categoryId }
        });
    },

    /** 获取视频详情 */
    getVideoDetail: (id) => {
        return request({
            url: `/study/video/${id}`,
            method: 'get'
        });
    }
};
