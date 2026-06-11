package com.example.blog.service.impl;

import com.example.blog.entity.Resume;
import com.example.blog.entity.ResumeItem;
import com.example.blog.entity.ResumeSection;
import com.example.blog.service.ResumeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PDF 导出服务 —— 将简历数据渲染为 PDF 文件，通过 HttpServletResponse 返回给前端下载。
 *
 * 整体流程：
 *   1. exportPdf() 从数据库获取简历完整数据（含模块和条目）
 *   2. 用 PDFBox 创建 PDDocument，逐层绘制标题 → 模块 → 条目内容
 *   3. 先保存到 ByteArrayOutputStream（内存缓冲），成功后再写入 response 输出流
 *      ★ 这样做的好处：如果 PDF 生成中途出错，response 不会被污染，用户体验更好
 *   4. 设置 Content-Disposition 触发浏览器下载
 *
 * 排版参数（单位：pt，1pt ≈ 0.35mm）：
 *   - A4 纸张：595.27 × 841.9 pt
 *   - 页边距：50 pt
 *   - 标题：字号 18，居中
 *   - 模块标题：字号 14，左对齐
 *   - 条目内容：字号 12，左缩进 20 pt
 */
@Service
public class PdfExportService {

    /* ======================== 排版常量 ======================== */

    // 页边距（缩小上边距以容纳更多内容）
    private static final float MARGIN = 40;

    // A4 纸张尺寸（PDFBox 标准定义）
    private static final float PAGE_W = PDRectangle.A4.getWidth();   // 595.27 pt
    private static final float PAGE_H = PDRectangle.A4.getHeight();  // 841.9  pt

    // 可用宽度 = 纸张宽度 - 左右边距
    private static final float AVAIL_W = PAGE_W - 2 * MARGIN;       // 515.27 pt

    // 右侧安全边距
    private static final float RIGHT_PADDING = 80;

    // 字号（缩小字号适配一页）
    private static final float TITLE_SIZE   = 22;  // 简历大标题
    private static final float SECTION_SIZE = 13;  // 模块标题
    private static final float ITEM_SIZE    = 11;  // 条目内容

    // 行高（缩小行距使排版更紧凑）
    private static final float LEADING = 16;

    private static final Logger log = LoggerFactory.getLogger(PdfExportService.class);

    /**
     * Jackson 的 ObjectMapper，用于将 item.content（JSON 字符串）解析为 Map。
     * 相比手动拼接 JSON，用 Jackson 更安全、不易出错。
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ResumeService resumeService;

    /* ============================================================
     *  入口方法 —— 对外暴露的唯一 public 方法
     * ============================================================ */

    /**
     * 导出简历 PDF 到 HttpServletResponse。
     *
     * 设计要点（★ 非常重要）：
     *   1. PDF 先生成到 ByteArrayOutputStream（内存缓冲），全部成功后再写入 response
     *      → 如果生成过程抛异常，response 输出流不会被写入，Spring 全局异常处理器可以正常返回 JSON 错误
     *      → 如果直接写入 response.getOutputStream()，异常时 response 已经写了部分数据，无法回滚
     *   2. try-with-resources 自动关闭 PDDocument，无需手动 close()
     *   3. Content-Disposition 同时设置 filename（ASCII 兜底）和 filename*（UTF-8 真名）
     *   4. ★ 生成后使用 PDDocument.load() 重新加载验证文件完整性，提前发现损坏
     */
    public void exportPdf(Long resumeId, HttpServletResponse response) throws IOException {
        Resume resume = resumeService.getResumeDetail(resumeId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);// 内存缓冲
        // STEP 1: 用 PDFBox 生成简历的 PDF
        try (PDDocument doc = new PDDocument()) {
            PDFont font = loadFont(doc);            // 正文字体（普通）
            PDFont boldFont = loadBoldFont(doc);    // 加粗字体（标题/模块）
            log.info("字体加载完成: 正文={}, 粗体={}", font.getName(), boldFont != null ? boldFont.getName() : "无");
            renderDocument(doc, font, boldFont, resume);
            doc.save(baos);//
        }

        // STEP 2.5: PDF 完整性验证
        // 用 PDDocument.load() 重新加载生成的字节，如果能成功打开说明文件结构完整
        byte[] pdfData = baos.toByteArray();
        log.info("PDF 生成完成，大小: {} 字节", pdfData.length);
        if (pdfData.length == 0) {
            throw new IOException("PDF 生成失败：生成内容为空");
        }
        // 验证文件头是否为 %PDF-
        String header = new String(pdfData, 0, Math.min(8, pdfData.length), StandardCharsets.US_ASCII);
        if (!header.startsWith("%PDF-")) {
            log.error("PDF 文件头异常: {}", header);
            throw new IOException("PDF 文件格式异常，文件头不是 %PDF-");
        }
        // 用 PDFBox 重新加载验证完整性
        try (PDDocument ignored = PDDocument.load(new ByteArrayInputStream(pdfData),
                org.apache.pdfbox.io.MemoryUsageSetting.setupMainMemoryOnly())) {
            log.info("PDF 完整性验证通过，共 {} 页", ignored.getNumberOfPages());
        } catch (IOException e) {
            log.error("PDF 完整性验证失败", e);
            throw new IOException("PDF 完整性验证失败，文件已损坏", e);
        }

        // STEP 3: 设置响应头，告诉浏览器这是 PDF 附件
        // 文件名校验：去掉 Windows 文件名中不允许的字符
        String safeName = resume.getName().replaceAll("[\\\\/:*?\"<>|]", "_");
        // RFC 5987 编码：URLEncoder 默认把空格编码为 +，但 RFC 5987 要求空格编码为 %20
        String encoded = URLEncoder.encode(safeName + ".pdf", StandardCharsets.UTF_8)
                .replace("+", "%20");
        response.setContentType("application/pdf");
        response.setContentLength(pdfData.length);          // 告诉浏览器文件大小（进度条用）
        response.setHeader("Content-Disposition",
                "attachment; filename=\"resume.pdf\"; filename*=UTF-8''" + encoded);

        // STEP 4: 将内存中的 PDF 字节写入 response 输出流
        // ★ 到这一步才接触 response 输出流，确保前面出错时 response 是干净的
        try (OutputStream os = response.getOutputStream()) {
            os.write(pdfData);
            os.flush();
        }
    }

    /* ============================================================
     *  PDF 文档渲染
     * ============================================================ */

    /**
     * 渲染整份简历到 PDF 文档。
     *
     * ★ RenderState 模式：
     *   创建一个 RenderState 对象持有当前页码的 contentStream、y 坐标等可变状态。
     *   所有绘制方法都接收 RenderState，修改其中的 y 坐标和 cs。
     *   这样避免了方法的参数过长，也方便 ensureSpace() 在分页时直接替换 state.cs。
     *
     * ★ contentStream 关闭时机：
     *   在 finally 块中关闭 st.cs，确保即使绘制中途抛出异常，流也能被正确关闭。
     *   PDFBox 要求 contentStream 必须在 document.save() 之前关闭，
     *   否则保存的 PDF 结构不完整，打不开。
     */
    private void renderDocument(PDDocument doc, PDFont font, PDFont boldFont, Resume resume) throws IOException {
        // 创建第一页
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);
        PDPageContentStream cs = new PDPageContentStream(doc, page);

        // RenderState 封装了所有可变状态，方便分页时替换 cs 和 y
        RenderState st = new RenderState(cs, doc, font, boldFont, PAGE_H - MARGIN);

        try {
            log.debug("开始绘制简历: {}", resume.getName());

            // 绘制简历标题（使用粗体 + 大字号）
            PDFont titleFont = st.boldFont != null ? st.boldFont : font;
            cs.setFont(titleFont, TITLE_SIZE);
            drawTitle(st, resume.getName());
            st.y -= 10;  // 标题与第一个模块之间的间距

            // 循环绘制每个模块
            log.debug("简历模块数量: {}", resume.getSections() != null ? resume.getSections().size() : 0);
            if (resume.getSections() != null) {
                for (ResumeSection section : resume.getSections()) {
                    drawSection(st, section);
                }
            }
        } catch (Exception e) {
            // 记录具体的绘制异常，方便排查
            log.error("PDF 绘制过程中发生异常: {}", e.getMessage(), e);
            throw e;  // 重新抛出，由上层处理
        } finally {
            // ★★★ 关键：必须在 finally 中关闭 contentStream，避免异常导致流未关闭 ★★★
            // 关闭后，document.save() 才能生成完整的 PDF
            try {
                st.cs.close();
            } catch (Exception e) {
                log.warn("关闭 contentStream 时发生异常", e);
            }
        }
    }

    /* ============================================================
     *  字体加载
     * ============================================================ */

    /**
     * 加载支持中文的字体。
     *
     * 加载顺序（优先选用 TTF 格式，避免 TTC 解析兼容性问题）：
     *   1. msyh.ttf   → 微软雅黑（TTF 格式，PDType0Font.load 直接支持）
     *   2. simsun.ttc → 宋体（TTC 集合格式，部分 PDFBox 版本支持直接加载）
     *   3. simfang.ttf → 仿宋（兜底 TTF）
     *   4. HELVETICA  → PDFBox 内置字体（不含中文字形，仅用于排除字体不存在导致的异常）
     *
     * ★ 为什么先试 msyh.ttf？
     *   在 PDFBox 2.x 中，PDType0Font.load() 内部使用 TTFParser 解析字体文件。
     *   TTC（TrueType Collection）是多个 TTF 的打包集合，需要特殊解析，
     *   而 TTF 是单个字体文件，PDType0Font.load() 可以直接处理。
     *   微软雅黑（msyh.ttf）是 TTF 格式，兼容性最好。
     */
    private PDFont loadBoldFont(PDDocument doc) {
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return null;
        }
        String[] boldCandidates = {
            "C:\\Windows\\Fonts\\msyhbd.ttf",      // 微软雅黑粗体
            "C:\\Windows\\Fonts\\msyh.ttf",         // 降级：普通体
            "C:\\Windows\\Fonts\\simfang.ttf"
        };
        for (String path : boldCandidates) {
            File f = new File(path);
            if (!f.exists()) continue;
            try {
                PDFont font = PDType0Font.load(doc, f);
                log.info("粗体字体加载成功: {}", path);
                return font;
            } catch (Exception e) {
                log.warn("粗体字体 {} 加载失败", path, e);
            }
        }
        log.warn("所有粗体字体加载失败");
        return null;
    }

    private PDFont loadFont(PDDocument doc) {
        // 非 Windows 系统不尝试加载本地字体（路径是 Windows 专属）
        if (!System.getProperty("os.name").toLowerCase().contains("win")) {
            return PDType1Font.HELVETICA;
        }

        // 按优先级从高到低排列字体候选（优先使用 TTF，避免 TTC 兼容问题）
        String[] candidates = {
            "C:\\Windows\\Fonts\\msyh.ttf",      // 微软雅黑（TTF）
            "C:\\Windows\\Fonts\\msyhbd.ttf",     // 微软雅黑粗体
            "C:\\Windows\\Fonts\\simfang.ttf",    // 仿宋（TTF）
            "C:\\Windows\\Fonts\\msyhl.ttf",      // 微软雅黑细体
            "C:\\Windows\\Fonts\\simsun.ttc",     // 宋体（TTC，兜底，部分 PDFBox 版本不支持）
        };

        for (String path : candidates) {
            File f = new File(path);
            if (!f.exists()) continue;  // 字体文件不存在，跳过
            try {
                // PDType0Font.load 会读取字体子集嵌入 PDF，确保没有字体的设备也能正确显示
                PDFont font = PDType0Font.load(doc, f);
                log.info("字体加载成功: {} (RIGHT_PADDING={})", path, RIGHT_PADDING);
                return font;
            } catch (Exception e) {
                // 单个字体加载失败不影响其他候选字体的尝试
                log.warn("字体 {} 加载失败", path, e);
            }
        }

        // 所有中文字体都加载失败，使用 HELVETICA 兜底
        // 注意：Helvetica 不含中文，如果简历中有中文内容，渲染时会抛异常
        log.warn("所有中文字体加载失败，使用HELVETICA（中文内容将无法显示）");
        return PDType1Font.HELVETICA;
    }

    /* ============================================================
     *  绘制方法
     * ============================================================ */

    /**
     * 绘制一个模块（如"教育背景"、"工作经历"）。
     *
     * 绘制顺序：
     *   1. 调用 ensureSpace() 检查是否有足够空间，不够则新建一页
     *   2. 调用 beginText() / endText() 输出模块标题
     *   3. 遍历模块下的条目，逐个调用 drawItem()
     *
     * ★ beginText() / endText() 配对原则：
     *   PDFBox 中，showText() 必须在 beginText() 之后、endText() 之前调用。
     *   本文所有绘制方法都严格遵守这个原则，beginText() 和 endText() 在同一方法中成对出现。
     *   ★ 绝对不要在 beginText() 和 endText() 之间调用 ensureSpace()，
     *     因为 ensureSpace() 会关闭当前的 contentStream，导致 endText() 在新流上找不到 beginText()。
     */
    private void drawSection(RenderState st, ResumeSection section) throws IOException {
        // ★ 在 beginText() 之前检查空间，避免在文本块中分页
        ensureSpace(st, SECTION_SIZE + 40);
        st.y -= 18;  // 模块与上一内容的间距（紧凑）

        // 输出模块标题（使用粗体 + 分隔横线）
        PDFont sectionFont = st.boldFont != null ? st.boldFont : st.font;
        String label = getSectionLabel(section);
        List<String> titleLines = wrapText(sectionFont, label, SECTION_SIZE, AVAIL_W - RIGHT_PADDING);
        String displayLabel = titleLines.isEmpty() ? label : titleLines.get(0);
        st.cs.setFont(sectionFont, SECTION_SIZE);
        st.cs.beginText();
        st.cs.setHorizontalScaling(98);
        st.cs.newLineAtOffset(MARGIN, st.y);
        st.cs.showText(displayLabel);
        st.cs.endText();
        // 标题与横线的间距
        st.y -= SECTION_SIZE + 5;
        // 绘制分隔横线（加粗纯黑，与模块标题形成清晰层次）
        st.cs.setStrokingColor(0, 0, 0);
        st.cs.setLineWidth(1.5f);
        st.cs.moveTo(MARGIN, st.y);          // 起点：左边距
        st.cs.lineTo(PAGE_W - MARGIN, st.y); // 终点：右边距
        st.cs.stroke();                      // 执行绘制
        // 横线与第一条目的间距（紧凑）
        st.y -= 8;

        // 恢复正文字体，后续条目内容使用普通字重
        st.cs.setFont(st.font, ITEM_SIZE);

        // 遍历条目
        if (section.getItems() != null) {
            for (ResumeItem item : section.getItems()) {
                drawItem(st, item, section.getSectionType());
            }
        }
    }

    /**
     * 绘制一个条目。
     *
     * ★ 通用 title + description 格式：
     *   title（加粗，主文字）→ 如 "清华大学 | 计算机"
     *   description（正常，详情）→ 如 "2020-2024，学习了..."
     *   所有模块类型统一使用此格式，不再按类型区分字段。
     */
    private void drawItem(RenderState st, ResumeItem item, String type) throws IOException {
        String title = "";
        String desc = "";
        try {
            Map<String, Object> map = MAPPER.readValue(item.getContent(), Map.class);
            title = str(map.get("title"));
            desc = str(map.get("description"));
            // 兼容旧格式：没有 title/description 时从旧字段映射
            if (title.isEmpty() && desc.isEmpty()) {
                switch (type) {
                    case "EDUCATION":
                        title = joinFields(map, "school", "major", "period");
                        break;
                    case "WORK":
                        title = joinFields(map, "company", "position", "period");
                        desc = str(map.get("description"));
                        break;
                    case "PROJECT":
                        title = joinFields(map, "name", "role", "tech");
                        desc = str(map.get("description"));
                        break;
                    case "SKILL":
                        Object skills = map.get("skills");
                        if (skills instanceof List) {
                            title = String.join("、", (List<String>) skills);
                        }
                        break;
                    case "OBJECT":
                        title = str(map.get("text"));
                        break;
                }
            }
        } catch (Exception e) {
            String t = item.getContent();
            if (t != null && !t.trim().isEmpty()) title = t;
        }

        // 估算纵向总高度
        float needed = 0;
        if (!title.isEmpty()) {
            needed += 2 + countLines(st.font, title) * LEADING;
        }
        if (!desc.isEmpty()) {
            needed += 2 + countLines(st.font, desc) * LEADING;  // desc 也有独立间距
        }
        if (needed <= 0) return;
        ensureSpace(st, needed);

        // title 加粗绘制
        if (!title.isEmpty()) {
            st.y -= 2;
            PDFont titleFont = st.boldFont != null ? st.boldFont : st.font;
            st.cs.setFont(titleFont, ITEM_SIZE);
            drawText(st, title, ITEM_SIZE, 15);
            st.cs.setFont(st.font, ITEM_SIZE);  // 恢复常规字体
        }
        // description 正常字体绘制
        if (!desc.isEmpty()) {
            st.y -= 2;
            drawText(st, desc, ITEM_SIZE, 25);  // 缩进更多，区分层次
        }
    }

    /**
     * 绘制一段文本（自动换行）。
     *
     * ★ beginText() / endText() 在这里成对出现，中间只做 showText 和换行。
     *   不在这里调用 ensureSpace()，确保文本块的完整性。
     */
    private void drawText(RenderState st, String text, float size, float indent) throws IOException {
        // ★ 空文本跳过绘制
        if (text == null || text.trim().isEmpty()) return;

        // ★ 计算安全宽度：预留 3pt 右侧边距，最大限度利用页面宽度
        float safeMaxW = AVAIL_W - indent - 3;
        List<String> lines = wrapText(st.font, text, size, safeMaxW);
        if (lines.isEmpty()) return;

        float startX = MARGIN + indent;
        float maxRightX = PAGE_W - MARGIN;

        for (String line : lines) {
            float lineW = st.font.getStringWidth(line) / 1000f * size;
            float endX = startX + lineW;
            if (endX > maxRightX) {
                log.error("行宽度溢出!! 文本='{}' 宽度={}pt 起点={}pt 终点={}pt 右边界={}pt 超出={}pt",
                        line, Math.round(lineW), Math.round(startX),
                        Math.round(endX), Math.round(maxRightX), Math.round(endX - maxRightX));
            }

            st.cs.beginText();
            st.cs.setHorizontalScaling(98);  // ★ 略微压缩，补偿 getStringWidth 与 showText 的微小偏差
            st.cs.newLineAtOffset(startX, st.y);
            st.cs.showText(line);
            st.cs.endText();
            st.y -= LEADING;
        }
    }

    /**
     * 绘制居中的标题。
     *
     * 居中原理：
     *   1. font.getStringWidth(text) 获取文本的宽度（单位：1/1000 em）
     *   2. 除以 1000 再乘以字号，得到文本的实际宽度（pt）
     *   3. 起始 x = (纸张宽度 - 文本宽度) / 2
     */
    private void drawTitle(RenderState st, String text) throws IOException {
        // 简历大标题使用粗体（如果可用）
        PDFont titleFont = st.boldFont != null ? st.boldFont : st.font;
        float w = titleFont.getStringWidth(text) / 1000f * TITLE_SIZE;
        // ★ 居中，但限制左右不超出边距（超长标题会被挤到左侧对齐）
        float x = (PAGE_W - w) / 2;
        x = Math.max(x, MARGIN);                      // 左边界：不超出左边距
        x = Math.min(x, PAGE_W - MARGIN - w);          // 右边界：不超出右边距
        x = Math.max(x, MARGIN);                       // 兜底：至少从左边距开始
        st.cs.beginText();
        st.cs.setHorizontalScaling(98);
        st.cs.newLineAtOffset(x, st.y);
        st.cs.showText(text);
        st.cs.endText();
    }

    /* ============================================================
     *  布局辅助方法
     * ============================================================ */

    /**
     * 检查当前页剩余空间是否足够，不够则新建一页。
     *
     * 调用规则（★ 非常重要）：
     *   只能在 beginText() 之前调用，绝对不能在一个文本块（beginText~endText）中间调用。
     *   原因：ensureSpace 会关闭当前 contentStream 并打开一个新的，
     *   如果此时正处于 beginText ~ endText 之间，新流上直接调用 endText()
     *   会因为没有对应的 beginText() 而抛出异常，生成的 PDF 也会损坏。
     */
    private void ensureSpace(RenderState st, float needed) throws IOException {
        // 剩余空间足够，无需分页
        if (st.y - needed >= MARGIN) return;

        // ★ 关闭当前页的 contentStream（不在 finally 中，因为调用者负责出错时的关闭）
        st.cs.close();

        // 创建新页面和新的 contentStream
        PDPage page = new PDPage(PDRectangle.A4);
        st.doc.addPage(page);
        st.cs = new PDPageContentStream(st.doc, page);

        // 新页面的字体和 y 坐标复位
        st.cs.setFont(st.font, ITEM_SIZE);
        st.y = PAGE_H - MARGIN;  // y 回到页面顶部
    }

    /**
     * 估算一段文本需要占用的行数（用于提前判断是否需要分页）。
     *
     * 与 drawText 使用相同的 wrapText 逻辑，确保估算结果与实际渲染一致。
     * 最少返回 1（即使文本为空），避免 needed = 0 导致 ensureSpace 失效。
     */
    private int countLines(PDFont font, String text) throws IOException {
        // ★ 与 drawText 使用相同的宽度计算，确保估算高度与实际一致
        return Math.max(wrapText(font, text, ITEM_SIZE, AVAIL_W - 15 - 8).size(), 1);
    }

    /**
     * 文本自动换行实现 —— 逐字符遍历，超宽即换行。
     *
     * ★ 为什么不用 split(" ") 按单词拆分？
     *   中文没有空格分隔，整段中文会被当成一个单词，无法在字符边界断行。
     *   改用逐字符遍历后，无论中文、英文、混合文本，都保证每行宽度 ≤ maxW。
     *
     * ★ 保留用户手动换行：
     *   先用 \n 分段，每段内部逐字符检查宽度，超限则换行。
     *
     * @param font  用于计算宽度的字体
     * @param text  要拆分的文本
     * @param size  字号
     * @param maxW  单行最大宽度（pt）
     * @return      拆分后的行列表
     */
    private List<String> wrapText(PDFont font, String text, float size, float maxW) throws IOException {
        // ★ 硬限制：任何情况下文本都不能超出右边界 MARGIN
        maxW = Math.min(maxW, PAGE_W - 2 * MARGIN - 40);
        List<String> result = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            result.add("");
            return result;
        }

        // 按换行符分段，保留用户手动换行
        for (String para : text.split("\n", -1)) {
            if (para.isEmpty()) {
                result.add("");
                continue;
            }

            StringBuilder line = new StringBuilder();
            for (int i = 0; i < para.length(); i++) {
                char c = para.charAt(i);
                // 测试加入当前字符后的宽度
                float w = font.getStringWidth(line.toString() + c) / 1000f * size;
                if (w > maxW && line.length() > 0) {
                    result.add(line.toString());
                    line = new StringBuilder(String.valueOf(c));
                } else {
                    line.append(c);
                }
            }
            if (!line.isEmpty()) {
                result.add(line.toString());
            }
        }
        return result;
    }

    /* ============================================================
     *  静态工具方法
     * ============================================================ */

    /**
     * 安全获取字符串，null 转为空字符串并去空格。 */
    private static String joinFields(Map<String, Object> map, String... keys) {
        List<String> parts = new ArrayList<>();
        for (String k : keys) {
            String v = str(map.get(k));
            if (!v.isEmpty()) parts.add(v);
        }
        return String.join(" · ", parts);
    }

    private static String str(Object o) {
        if (o == null) return "";
        String s = o.toString().trim();
        return s.isEmpty() || "null".equalsIgnoreCase(s) ? "" : s;
    }

    /** 将 sectionType 转为中文显示名。OBJECT 类型的模块使用 customLabel 字段。 */
    private static String getSectionLabel(ResumeSection s) {
        if ("OBJECT".equals(s.getSectionType()) && s.getCustomLabel() != null) {
            return s.getCustomLabel();  // 用户自定义模块名，如"荣誉奖项"
        }
        switch (s.getSectionType()) {
            case "EDUCATION": return "教育背景";
            case "WORK":      return "工作经历";
            case "PROJECT":   return "项目经历";
            case "SKILL":     return "技能证书";
            default:          return s.getSectionType();  // 未知类型直接显示英文
        }
    }

    /* ============================================================
     *  可变状态类
     * ============================================================ */

    /**
     * PDF 渲染过程中的可变状态。
     *
     * 为什么需要这个类而不是直接在方法中传参？
     *   - cs（当前流）和 y（当前 y 坐标）在分页时会被修改
     *   - 用对象包裹后，ensureSpace() 可以直接替换 st.cs，调用方无需处理返回值
     *   - doc 和 font 在渲染过程中不变，也放在这里避免参数列表过长
     *
     * 字段说明：
     *   cs    —— 当前页的 PDPageContentStream，分页时会被替换为新页的流
     *   doc   —— PDDocument，创建新页面时需要
     *   font  —— 字体，各页统一使用同一个字体
     *   y     —— 当前绘制位置的 y 坐标（从上往下递减，原点在页面左下角）
     */
    private static class RenderState {
        PDPageContentStream cs;
        final PDDocument doc;
        final PDFont font;           // 正文字体（普通）
        final PDFont boldFont;       // 加粗字体（标题/模块）
        float y;

        RenderState(PDPageContentStream cs, PDDocument doc, PDFont font, PDFont boldFont, float y) {
            this.cs = cs;
            this.doc = doc;
            this.font = font;
            this.boldFont = boldFont;
            this.y = y;
        }
    }
}
