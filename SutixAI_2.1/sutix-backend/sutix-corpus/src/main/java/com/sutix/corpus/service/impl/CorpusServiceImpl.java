package com.sutix.corpus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sutix.corpus.dto.DocumentVO;
import com.sutix.corpus.entity.CorpusDocument;
import com.sutix.corpus.entity.CorpusSentence;
import com.sutix.corpus.mapper.CorpusDocumentMapper;
import com.sutix.corpus.mapper.CorpusSentenceMapper;
import com.sutix.corpus.service.CorpusService;
import com.sutix.system.entity.SysUser;
import com.sutix.system.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CorpusServiceImpl extends ServiceImpl<CorpusDocumentMapper, CorpusDocument>
        implements CorpusService {

    @Resource
    private CorpusDocumentMapper documentMapper;

    @Resource
    private CorpusSentenceMapper sentenceMapper;

    @Override
    public List<CorpusDocument> listDocuments(Long categoryId) {
        QueryWrapper<CorpusDocument> wrapper = new QueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq("category_id", categoryId);
        }
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public DocumentVO getDocumentDetail(Long documentId) {
        CorpusDocument doc = getById(documentId);
        if (doc == null) {
            throw new RuntimeException("文档不存在");
        }

        DocumentVO vo = new DocumentVO();
        BeanUtils.copyProperties(doc, vo);

        QueryWrapper<CorpusSentence> wrapper = new QueryWrapper<>();
        wrapper.eq("document_id", documentId).orderByAsc("seq");
        vo.setSentences(sentenceMapper.selectList(wrapper));

        return vo;
    }

    @Override
    @Transactional
    public Long addDocument(CorpusDocument document) {
        SysUser user = SecurityUtils.getUser();
        document.setCreateUser(user.getId());
        save(document);
        return document.getId();
    }

    @Override
    @Transactional
    public void addSentences(Long documentId, List<CorpusSentence> sentences) {
        for (CorpusSentence s : sentences) {
            s.setDocumentId(documentId);
            if (s.getSeq() == null) {
                s.setSeq(0);
            }
        }
        // 使用 sentenceMapper 批量插入（规避 ServiceImpl 泛型冲突）
        for (CorpusSentence s : sentences) {
            sentenceMapper.insert(s);
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        // 先删语料行
        QueryWrapper<CorpusSentence> wrapper = new QueryWrapper<>();
        wrapper.eq("document_id", documentId);
        sentenceMapper.delete(wrapper);
        // 再删文档
        removeById(documentId);
    }
}
