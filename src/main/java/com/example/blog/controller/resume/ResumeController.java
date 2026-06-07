package com.example.blog.controller.resume;

import com.example.blog.common.context.BaseContext;
import com.example.blog.common.result.Result;
import com.example.blog.entity.Resume;
import com.example.blog.model.dto.resume.*;
import com.example.blog.service.ResumeService;
import com.example.blog.service.impl.PdfExportService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 简历管理控制器
 * 提供简历的完整CRUD接口，包括模块和条目的嵌套管理。
 * 所有接口统一返回 Result 格式，使用 @Valid 进行参数校验。
 * 遵循 RESTful 风格：POST=新增，GET=查询，PUT=更新，DELETE=删除。
 * 所有接口通过 BaseContext.getCurrentId() 获取当前用户，前端无需传递 userId。
 *
 * API路径设计：
 *   /api/v1/resumes - 简历主资源
 *   /api/v1/resumes/{resumeId}/sections - 模块子资源
 *   /api/v1/resumes/{resumeId}/sections/{sectionId}/items - 条目子资源
 */
@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @Autowired
    private PdfExportService pdfExportService;

    /** 创建新简历 */
    @PostMapping("/resumes")
    public Result createResume(@Valid @RequestBody CreateResumeDto dto) {
        Resume resume = resumeService.createResume(dto);
        return Result.success("创建简历成功", resume);
    }

    /** 获取简历基本信息 */
    @GetMapping("/resumes/{resumeId}")
    public Result getResume(@PathVariable Long resumeId) {
        Resume resume = resumeService.getResumeDetail(resumeId);
        return Result.success(resume);
    }

    /** 获取简历完整详情（含所有模块和条目，按 sort_order 升序） */
    @GetMapping("/resumes/{resumeId}/detail")
    public Result getResumeDetail(@PathVariable Long resumeId) {
        Resume resume = resumeService.getResumeDetail(resumeId);
        return Result.success(resume);
    }

    /**
     * 导出简历为 PDF 文件，触发浏览器下载。
     *
     * ★ 为什么返回 void 而不是 Result？
     *   因为 PDF 是二进制文件，需要直接写入 HttpServletResponse 的输出流，
     *   而不是由 Spring 序列化为 JSON。返回 void 告诉 Spring：这个方法自己处理响应。
     *
     * ★ 为什么可以返回 void？
     *   因为 @RestController 默认会给所有方法加上 @ResponseBody，
     *   但对于返回 void 且参数中包含 HttpServletResponse 的方法，
     *   Spring MVC 会检测到响应已经被方法处理，不再尝试序列化返回值。
     *
     * 下载流程：
     *   1. 浏览器访问 GET /api/v1/resumes/{resumeId}/export/pdf
     *   2. PdfExportService.exportPdf() 调用 resumeService.getResumeDetail(resumeId) 获取数据
     *   3. 用 PDFBox 生成 PDF 到 ByteArrayOutputStream（内存）
     *   4. 设置 Content-Type: application/pdf，Content-Disposition: attachment
     *   5. 将 PDF 字节写入 response.getOutputStream()
     */
    @GetMapping("/resumes/{resumeId}/export/pdf")
    public void exportPdf(@PathVariable Long resumeId, HttpServletResponse response) throws IOException {
        pdfExportService.exportPdf(resumeId, response);
    }

    /** 更新简历基本信息 */
    @PutMapping("/resumes/{resumeId}")
    public Result updateResume(@PathVariable Long resumeId, @Valid @RequestBody UpdateResumeDto dto) {
        resumeService.updateResume(resumeId, dto);
        return Result.success("更新简历成功");
    }

    /** 删除整份简历（级联删除所有模块和条目） */
    @DeleteMapping("/resumes/{resumeId}")
    public Result deleteResume(@PathVariable Long resumeId) {
        resumeService.deleteResume(resumeId);
        return Result.success("删除简历成功");
    }

    /** 获取当前用户的所有简历列表 */
    @GetMapping("/resumes")
    public Result listCurrentUserResumes() {
        Object list = resumeService.listUserResumes();
        return Result.success(list);
    }

    /** 给指定简历添加一个模块 */
    @PostMapping("/resumes/{resumeId}/sections")
    public Result addSection(@PathVariable Long resumeId, @Valid @RequestBody CreateSectionDto dto) {
        Long sectionId = resumeService.addSection(resumeId, dto);
        return Result.success("添加模块成功", sectionId);
    }

    /** 更新指定模块 */
    @PutMapping("/resumes/{resumeId}/sections/{sectionId}")
    public Result updateSection(@PathVariable Long resumeId, @PathVariable Long sectionId,
                                @Valid @RequestBody UpdateSectionDto dto) {
        resumeService.updateSection(resumeId, sectionId, dto);
        return Result.success("更新模块成功");
    }

    /** 删除指定模块（级联删除其下所有条目） */
    @DeleteMapping("/resumes/{resumeId}/sections/{sectionId}")
    public Result deleteSection(@PathVariable Long resumeId, @PathVariable Long sectionId) {
        resumeService.deleteSection(resumeId, sectionId);
        return Result.success("删除模块成功");
    }

    /** 在指定模块下添加一个条目 */
    @PostMapping("/resumes/{resumeId}/sections/{sectionId}/items")
    public Result addItem(@PathVariable Long resumeId, @PathVariable Long sectionId,
                          @Valid @RequestBody CreateItemDto dto) {
        Long itemId = resumeService.addItem(resumeId, sectionId, dto);
        return Result.success("添加条目成功", itemId);
    }

    /** 更新指定条目 */
    @PutMapping("/resumes/{resumeId}/sections/{sectionId}/items/{itemId}")
    public Result updateItem(@PathVariable Long resumeId, @PathVariable Long sectionId,
                             @PathVariable Long itemId, @Valid @RequestBody UpdateItemDto dto) {
        resumeService.updateItem(resumeId, sectionId, itemId, dto);
        return Result.success("更新条目成功");
    }

    /** 删除指定条目 */
    @DeleteMapping("/resumes/{resumeId}/sections/{sectionId}/items/{itemId}")
    public Result deleteItem(@PathVariable Long resumeId, @PathVariable Long sectionId,
                             @PathVariable Long itemId) {
        resumeService.deleteItem(resumeId, sectionId, itemId);
        return Result.success("删除条目成功");
    }
}
