import request from '@/utils/request';

export const termApi = {

    favorite: (termId) => request({ url: `/term/favorite/${termId}`, method: 'post' }),

    unfavorite: (termId) => request({ url: `/term/favorite/${termId}`, method: 'delete' }),

    listFavorite: () => request({ url: '/term/favorite/list', method: 'get' }),

    favoriteCount: () => request({ url: '/term/favorite/count', method: 'get' }),

    updateCategory: (data) => {
        return request({
            url: '/term/updateCategory',
            method: 'put',
            data
        })
    },
    // 新增详情接口
    getDetail: (id) => {
        return request({
            url: `/term/detail/${id}`, // 替换为实际后端接口地址
            method: 'get'
        })
    },
    // 分页查询术语列表
    pageList: (queryDTO) => {
        // GET请求，参数拼接到URL（匹配后端@GetMapping("/term/list")）
        return request({
            url: '/term/list',
            method: 'get',
            params: queryDTO // TermQueryDTO参数（分页、查询条件等）
        });
    },

    // 新增术语
    add: (formDTO) => {
        // POST请求，JSON体传参（匹配后端@PostMapping("/term/add") + @RequestBody）
        return request({
            url: '/term/add',
            method: 'post',
            data: formDTO // TermFormDTO参数
        });
    },

    // 编辑术语
    update: (formDTO) => {
        // PUT请求，JSON体传参（匹配后端@PutMapping("/term/update") + @RequestBody）
        return request({
            url: '/term/update',
            method: 'put',
            data: formDTO // TermFormDTO参数
        });
    },

    // 删除术语
    delete: (id) => {
        // DELETE请求，路径传参（匹配后端@DeleteMapping("/term/delete/{id}")）
        return request({
            url: `/term/delete/${id}`,
            method: 'delete'
        });
    }

};