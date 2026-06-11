package com.example.blog.service.impl;

import com.example.blog.entity.Resume;
import com.example.blog.entity.ResumeItem;
import com.example.blog.entity.ResumeSection;
import com.example.blog.service.ResumeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * HTML → PDF 导出服务。
 *
 * 使用 Thymeleaf 模板引擎将简历数据渲染为 XHTML，
 * 再通过 Flying Saucer 将 XHTML 转换为 PDF。
 *
 * 相比 PDFBox 手动坐标排版，HTML + CSS 的方式更灵活、更容易控制页面布局。
 */
@Service
public class HtmlToPdfService {

    private static final Logger log = LoggerFactory.getLogger(HtmlToPdfService.class);

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private TemplateEngine templateEngine;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 导出简历为 PDF（HTML → PDF 渲染）。
     */
    public ByteArrayOutputStream exportPdf(Long resumeId) throws Exception {
        Resume resume = resumeService.getResumeDetail(resumeId);

        // 1. 构建 Thymeleaf 数据模型
        Map<String, Object> model = new HashMap<>();
        model.put("resume", resume);
        model.put("sections", buildSections(resume));

        // 2. 渲染 HTML
        Context ctx = new Context(Locale.CHINESE, model);
        String html = templateEngine.process("resume-export", ctx);
        log.debug("HTML 渲染完成，长度: {} 字符", html.length());

        // 3. HTML → PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);

        // 注册中文字体（支持 Windows 宋体）
        registerChineseFont(renderer);

        renderer.layout();
        renderer.createPDF(baos);
        log.info("PDF 生成完成，大小: {} 字节", baos.size());

        return baos;
    }

    /**
     * 构建 Thymeleaf 模板用的模块数据，包含已解析的条目内容。
     * 兼容新旧两种数据格式：优先使用 title/description，不存在则从旧字段映射。
     */
    private List<Map<String, Object>> buildSections(Resume resume) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (resume.getSections() == null) return result;

        for (ResumeSection section : resume.getSections()) {
            Map<String, Object> sectionMap = new LinkedHashMap<>();
            sectionMap.put("label", getSectionLabel(section));
            sectionMap.put("sectionType", section.getSectionType());

            List<Map<String, Object>> itemList = new ArrayList<>();
            if (section.getItems() != null) {
                for (ResumeItem item : section.getItems()) {
                    Map<String, Object> parsed = parseContent(item.getContent());
                    // 兼容旧格式：没有 title/description 时从旧字段映射
                    if (!parsed.containsKey("title") && !parsed.containsKey("description")) {
                        parsed = migrateOldFormat(parsed, section.getSectionType());
                    }
                    parsed.put("sectionType", section.getSectionType());
                    itemList.add(parsed);
                }
            }
            sectionMap.put("items", itemList);
            result.add(sectionMap);
        }
        return result;
    }

    /**
     * 将旧格式的字段映射为新的 title/description 格式。
     * 旧格式：school|major|period / company|position|period / name|role|tech
     */
    private Map<String, Object> migrateOldFormat(Map<String, Object> old, String type) {
        Map<String, Object> result = new HashMap<>();
        List<String> titleFields = new ArrayList<>();
        String desc = "";

        switch (type) {
            case "EDUCATION":
                addIfPresent(titleFields, old, "school", "major");
                desc = str(old.get("period"));
                break;
            case "WORK":
                addIfPresent(titleFields, old, "company", "position");
                desc = str(old.get("description"));
                break;
            case "PROJECT":
                addIfPresent(titleFields, old, "name", "role");
                desc = str(old.get("description"));
                break;
            case "SKILL":
                Object skills = old.get("skills");
                if (skills instanceof List) {
                    titleFields.add(String.join("、", (List<String>) skills));
                }
                break;
            default:
                titleFields.add(str(old.get("text")));
                break;
        }

        if (!titleFields.isEmpty()) result.put("title", String.join(" · ", titleFields));
        if (!desc.isEmpty()) result.put("description", desc);
        return result;
    }

    private void addIfPresent(List<String> list, Map<String, Object> map, String... keys) {
        for (String k : keys) {
            String v = str(map.get(k));
            if (!v.isEmpty()) list.add(v);
        }
    }

    private static String str(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /**
     * 解析条目 JSON 内容为 Map，方便 Thymeleaf 模板按 key 访问。
     */
    private Map<String, Object> parseContent(String content) {
        if (content == null || content.isBlank()) return new HashMap<>();
        try {
            return objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("条目 JSON 解析失败: {}", e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("text", content);
            return fallback;
        }
    }

    /**
     * 注册中文字体到 Flying Saucer 字体解析器。
     * 优先使用 TTF 格式（msyh.ttf），失败则尝试 TTC（simsun.ttc）。
     */
    private void registerChineseFont(ITextRenderer renderer) {
        String[][] candidates = {
            {"C:\\Windows\\Fonts\\msyh.ttf", "SimSun"},
            {"C:\\Windows\\Fonts\\simsun.ttc", "SimSun"},
            {"C:\\Windows\\Fonts\\simfang.ttf", "SimSun"}
        };

        for (String[] candidate : candidates) {
            try {
                renderer.getFontResolver().addFont(candidate[0],
                        com.lowagie.text.pdf.BaseFont.IDENTITY_H,
                        com.lowagie.text.pdf.BaseFont.EMBEDDED);
                log.info("中文字体注册成功: {}", candidate[0]);
                return;
            } catch (Exception e) {
                log.warn("字体注册失败: {}", candidate[0], e);
            }
        }
        log.warn("所有中文字体注册均失败，PDF 中文可能无法正确显示");
    }

    private static String getSectionLabel(ResumeSection s) {
        if ("OBJECT".equals(s.getSectionType()) && s.getCustomLabel() != null) {
            return s.getCustomLabel();
        }
        switch (s.getSectionType()) {
            case "EDUCATION": return "教育背景";
            case "WORK":      return "工作经历";
            case "PROJECT":   return "项目经历";
            case "SKILL":     return "技能证书";
            default:          return s.getSectionType();
        }
    }
}
