package com.sutix.corpus.service;

import com.sutix.corpus.dto.DocumentVO;
import com.sutix.corpus.entity.CorpusDocument;
import com.sutix.corpus.entity.CorpusSentence;

import java.util.List;

public interface CorpusService {
    /** 获取文档列表（可按分类筛选） */
    List<CorpusDocument> listDocuments(Long categoryId);

    /** 获取文档详情（含双语语料行） */
    DocumentVO getDocumentDetail(Long documentId);

    /** 新增文档 */
    Long addDocument(CorpusDocument document);

    /** 新增语料行 */
    void addSentences(Long documentId, List<CorpusSentence> sentences);

    /** 删除文档及所有语料行 */
    void deleteDocument(Long documentId);
}
