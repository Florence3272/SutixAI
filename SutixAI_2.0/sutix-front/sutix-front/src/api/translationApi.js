import request from '@/utils/request';

/** 创建带长时间超时的请求实例（翻译可能耗时30秒+） */
const longRequest = (config) => {
    return request({
        ...config,
        timeout: 120000  // 2分钟超时
    });
};

/**
 * 翻译模块 API
 */
export const translationApi = {
    /** 文本翻译（长超时，等待后端轮询外部API） */
    translateText: (text, direction) => {
        const params = new URLSearchParams();
        params.append('text', text);
        params.append('direction', direction);
        return longRequest({
            url: '/translation/translate',
            method: 'post',
            data: params,
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
        });
    },

    /** 上传文档翻译（长超时） */
    uploadDocument: (formData) => {
        return longRequest({
            url: '/translation/upload',
            method: 'post',
            data: formData,
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    /** 查询翻译进度 */
    getProgress: (id) => {
        return request({
            url: `/translation/progress/${id}`,
            method: 'get'
        });
    },

    /** 获取翻译历史 */
    getHistory: () => {
        return request({
            url: '/translation/history',
            method: 'get'
        });
    },

    /** 解析文件内容 */
    parseFile: (formData) => {
        return request({
            url: '/translation/parse',
            method: 'post',
            data: formData,
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
};
