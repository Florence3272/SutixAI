package com.sutix.corpus.controller;

import com.sutix.common.annotation.Admin;
import com.sutix.common.result.Result;
import com.sutix.corpus.dto.DocumentVO;
import com.sutix.corpus.entity.CorpusDocument;
import com.sutix.corpus.service.CorpusService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/corpus")
public class CorpusController {

    @Resource
    private CorpusService corpusService;

    /** 获取文档列表 */
    @GetMapping("/document/list")
    public Result<List<CorpusDocument>> listDocuments(
            @RequestParam(required = false) Long categoryId) {
        return Result.success(corpusService.listDocuments(categoryId));
    }

    /** 获取文档详情 */
    @GetMapping("/document/{id}")
    public Result<DocumentVO> getDocumentDetail(@PathVariable Long id) {
        return Result.success(corpusService.getDocumentDetail(id));
    }

    /** 新增文档（管理员） */
    @Admin
    @PostMapping("/document/add")
    public Result<Long> addDocument(@RequestBody CorpusDocument document) {
        return Result.success(corpusService.addDocument(document));
    }

    /** 删除文档及所有语料行（管理员） */
    @Admin
    @DeleteMapping("/document/{id}")
    public Result<Void> deleteDocument(@PathVariable Long id) {
        corpusService.deleteDocument(id);
        return Result.success();
    }
}
