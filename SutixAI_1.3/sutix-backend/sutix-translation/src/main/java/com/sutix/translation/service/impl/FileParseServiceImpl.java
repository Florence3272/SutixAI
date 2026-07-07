package com.sutix.translation.service.impl;

import com.sutix.translation.service.FileParseService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class FileParseServiceImpl implements FileParseService {

    @Override
    public String parseFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }

        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".txt")) {
            return parseTxt(file);
        } else if (lowerName.endsWith(".doc")) {
            return parseDoc(file);
        } else if (lowerName.endsWith(".docx")) {
            return parseDocx(file);
        } else if (lowerName.endsWith(".pdf")) {
            return parsePdf(file);
        } else {
            throw new IllegalArgumentException("不支持的文件格式: " + fileName);
        }
    }

    private String parseTxt(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

    private String parseDoc(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             HWPFDocument doc = new HWPFDocument(is);
             WordExtractor extractor = new WordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String parseDocx(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }

    private String parsePdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }
}
