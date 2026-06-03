package com.example.blog.controller.resume;

import com.example.blog.common.context.BaseContext;
import com.example.blog.common.result.Result;
import com.example.blog.entity.Resume;
import com.example.blog.model.dto.resume.*;
import com.example.blog.service.ResumeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /** 创建新简历 */
    @PostMapping("/resumes")
    public Result createResume(@Valid @RequestBody CreateResumeDto dto) {
        Resume resume = resumeService.createResume(dto);
        return Result.success("创建简历成功", resume);
    }

    /** 获取简历详情（含所有模块和条目） */
    @GetMapping("/resumes/{resumeId}")
    public Result getResumeDetail(@PathVariable Long resumeId) {
        Resume resume = resumeService.getResumeDetail(resumeId);
        return Result.success("创建成功",resume);
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
