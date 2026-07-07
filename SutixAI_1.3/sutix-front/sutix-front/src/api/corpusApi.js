import request from '@/utils/request';

/**
 * 语料库模块 API
 */
export const corpusApi = {
    /** 获取文档列表（可按分类筛选） */
    listDocuments: (categoryId) => {
        return request({
            url: '/corpus/document/list',
            method: 'get',
            params: { categoryId }
        });
    },

    /** 获取文档详情（含双语语料行） */
    getDocumentDetail: (id) => {
        return request({
            url: `/corpus/document/${id}`,
            method: 'get'
        });
    },

    /** 新增文档 */
    addDocument: (data) => {
        return request({
            url: '/corpus/document/add',
            method: 'post',
            data
        });
    },

    /** 删除文档 */
    deleteDocument: (id) => {
        return request({
            url: `/corpus/document/${id}`,
            method: 'delete'
        });
    }
};
