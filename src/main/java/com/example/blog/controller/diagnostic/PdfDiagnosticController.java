package com.example.blog.controller.diagnostic;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * PDF 诊断控制器 —— 用于独立验证 PDFBox 环境是否正常。
 *
 * 使用场景：
 *   1. 简历 PDF 导出失败时，先调用 /diagnostic/pdf-test-minimal
 *      如果这个能打开，说明 PDFBox 本身和 JDK 环境没问题
 *   2. 再调用 /diagnostic/pdf-test-font
 *      如果这个打不开，说明字体加载有问题
 *   3. 如果两个都能打开，说明问题在业务数据或渲染逻辑中
 */
@RestController
@RequestMapping("/api/v1/diagnostic")
public class PdfDiagnosticController {

    private static final Logger log = LoggerFactory.getLogger(PdfDiagnosticController.class);

    /**
     * 最小化 PDF 测试 —— 仅用 HELVETICA（内置字体，无需加载任何文件），纯英文。
     * 如果能下载并正常打开，说明 PDFBox 2.0.28 在当前 JDK 21 环境下工作正常。
     */
    @GetMapping("/pdf-test-minimal")
    public void testMinimalPdf(HttpServletResponse response) throws IOException {
        log.info("===== 开始 PDF 最小化测试 =====");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA, 24);
                cs.beginText();
                cs.newLineAtOffset(100, 700);
                cs.showText("Hello PDFBox 2.0.28!");
                cs.endText();
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.beginText();
                cs.newLineAtOffset(100, 660);
                cs.showText("JDK: " + System.getProperty("java.version"));
                cs.endText();
                cs.beginText();
                cs.newLineAtOffset(100, 640);
                cs.showText("OS: " + System.getProperty("os.name"));
                cs.endText();
            }
            doc.save(baos);
        }

        byte[] pdfData = baos.toByteArray();
        log.info("最小化 PDF 生成完成: {} 字节, 文件头: {}", pdfData.length,
                new String(pdfData, 0, 5, StandardCharsets.US_ASCII));

        String encoded = URLEncoder.encode("pdf-test-minimal.pdf", StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/pdf");
        response.setContentLength(pdfData.length);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"test.pdf\"; filename*=UTF-8''" + encoded);
        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

    /**
     * 字体加载测试 —— 尝试加载系统宋体并写入中文。
     * 如果这个 PDF 能打开且正确显示中文，说明字体加载正常。
     * 如果打不开，说明字体加载或中文渲染有问题。
     */
    @GetMapping("/pdf-test-font")
    public void testFontPdf(HttpServletResponse response) throws IOException {
        log.info("===== 开始 PDF 字体测试 =====");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String fontInfo = "未加载任何字体";
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            // 尝试加载中文字体
            PDType0Font cnFont = null;
            String[] fontPaths = {
                "C:\\Windows\\Fonts\\msyh.ttf",
                "C:\\Windows\\Fonts\\simsun.ttc",
                "C:\\Windows\\Fonts\\simfang.ttf"
            };
            for (String path : fontPaths) {
                File f = new File(path);
                if (f.exists()) {
                    try {
                        cnFont = PDType0Font.load(doc, f);
                        fontInfo = "成功: " + f.getName() + " (" + path + ")";
                        log.info("字体加载成功: {}", fontInfo);
                        break;
                    } catch (Exception e) {
                        log.warn("字体加载失败: {} - {}", path, e.getMessage());
                    }
                } else {
                    log.warn("字体文件不存在: {}", path);
                }
            }

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // 第一行：字体信息
                if (cnFont != null) {
                    cs.setFont(cnFont, 14);
                    cs.beginText();
                    cs.newLineAtOffset(50, 750);
                    cs.showText("字体测试: " + fontInfo);
                    cs.endText();
                    cs.setFont(cnFont, 12);
                    cs.beginText();
                    cs.newLineAtOffset(50, 720);
                    cs.showText("中文测试：清华大学 | 计算机科学与技术 | 2020-2024");
                    cs.endText();
                } else {
                    cs.setFont(PDType1Font.HELVETICA, 14);
                    cs.beginText();
                    cs.newLineAtOffset(50, 750);
                    cs.showText("所有中文字体加载失败！请检查系统字体目录");
                    cs.endText();
                }

                // 第二行：使用 HELVETICA 的英文行（始终显示）
                cs.setFont(PDType1Font.HELVETICA, 12);
                cs.beginText();
                cs.newLineAtOffset(50, 680);
                cs.showText("This line uses HELVETICA (always works)");
                cs.endText();
            }

            doc.save(baos);
        }

        byte[] pdfData = baos.toByteArray();
        log.info("字体测试 PDF 生成完成: {} 字节", pdfData.length);

        String encoded = URLEncoder.encode("pdf-test-font.pdf", StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/pdf");
        response.setContentLength(pdfData.length);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"test-font.pdf\"; filename*=UTF-8''" + encoded);
        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }
}
