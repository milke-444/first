package com.example.blog.service;

import com.example.blog.entity.Resume;
import com.example.blog.model.dto.resume.*;

/**
 * 简历服务接口
 * 定义简历的增删改查、模块管理、条目管理三大类共11个API方法。
 * 所有删除操作在实现层进行级联处理，确保子数据一并清理。
 */
public interface ResumeService {

    /** 创建新简历，返回含完整关联数据的简历对象 */
    Resume createResume(CreateResumeDto dto);

    /** 获取简历详情（含所有模块和条目） */
    Resume getResumeDetail(Long resumeId);

    /** 更新简历基本信息（名称、模板、状态等） */
    void updateResume(Long resumeId, UpdateResumeDto dto);

    /** 删除整份简历（级联删除所有模块和条目） */
    void deleteResume(Long resumeId);

    /** 获取当前用户的所有简历列表（不含模块和条目详情） */
    Object listUserResumes();

    /** 给简历添加一个模块，返回新模块ID */
    Long addSection(Long resumeId, CreateSectionDto dto);

    /** 更新模块信息 */
    void updateSection(Long resumeId, Long sectionId, UpdateSectionDto dto);

    /** 删除模块（级联删除其下所有条目） */
    void deleteSection(Long resumeId, Long sectionId);

    /** 在指定模块下添加一个条目，返回新条目ID */
    Long addItem(Long resumeId, Long sectionId, CreateItemDto dto);

    /** 更新条目内容 */
    void updateItem(Long resumeId, Long sectionId, Long itemId, UpdateItemDto dto);

    /** 删除单个条目 */
    void deleteItem(Long resumeId, Long sectionId, Long itemId);
}
