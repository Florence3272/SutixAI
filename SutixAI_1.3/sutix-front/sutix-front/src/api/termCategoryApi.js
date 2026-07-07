import request from '@/utils/request';

/**
 * 术语分类相关接口
 */
export const termCategoryApi = {
    // 获取所有分类列表
    listAll: () => {
        // GET请求（匹配后端@GetMapping("/term/category/list")）
        return request({
            url: '/term/category/list',
            method: 'get'
        });
    },

    // 根据ID查询分类
    getById: (id) => {
        // GET请求，路径传参（匹配后端@GetMapping("/term/category/{id}")）
        return request({
            url: `/term/category/${id}`,
            method: 'get'
        });
    },

    // 新增分类
    save: (category) => {
        // POST请求，JSON体传参（匹配后端@PostMapping("/term/category/save") + @RequestBody）
        return request({
            url: '/term/category/save',
            method: 'post',
            data: category // TermCategory参数
        });
    },

    // 修改分类
    update: (category) => {
        // PUT请求，JSON体传参（匹配后端@PutMapping("/term/category/update") + @RequestBody）
        return request({
            url: '/term/category/update',
            method: 'put',
            data: category // TermCategory参数
        });
    },

    // 删除分类
    remove: (id) => {
        // DELETE请求，路径传参（匹配后端@DeleteMapping("/term/category/{id}")）
        return request({
            url: `/term/category/${id}`,
            method: 'delete'
        });
    },

    categoryCount: () => {
        return request({
            url: `/term/category/count`,
            method: 'get',
        });
    }


};