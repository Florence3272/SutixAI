package com.sutix.term.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sutix.term.entity.Term;
import com.sutix.term.dto.TermQueryDTO;
import com.sutix.term.dto.TermFormDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface TermService extends IService<Term> {
    /** 分页查询术语 */
    IPage<Term> pageList(TermQueryDTO queryDTO);

    /** 新增术语 */
    boolean addTerm(TermFormDTO formDTO);

    /** 编辑术语 */
    boolean updateTerm(TermFormDTO formDTO);

    /** 删除术语 */
    boolean deleteTerm(Long id);

}
