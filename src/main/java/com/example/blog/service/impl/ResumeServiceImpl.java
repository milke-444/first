package com.example.blog.service.impl;

import com.example.blog.common.context.BaseContext;
import com.example.blog.common.globalException.UserException;
import com.example.blog.entity.Resume;
import com.example.blog.entity.ResumeItem;
import com.example.blog.entity.ResumeSection;
import com.example.blog.mapper.ResumeItemMapper;
import com.example.blog.mapper.ResumeMapper;
import com.example.blog.mapper.ResumeSectionMapper;
import com.example.blog.model.dto.resume.*;
import com.example.blog.model.vo.common.ExceptionCommon;
import com.example.blog.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 简历服务实现类
 * 核心设计思路：
 * 1. 所有写操作（增/删/改）都先查询数据是否存在，不存在则抛 UserException
 * 2. 删除操作采用@Transactional事务保证级联删除的原子性
 * 3. 更新操作使用 MyBatis 动态SQL，只更新非空字段
 */
@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeMapper resumeMapper;

    @Autowired
    private ResumeSectionMapper resumeSectionMapper;

    @Autowired
    private ResumeItemMapper resumeItemMapper;

    /**
     * 创建新简历
     * 新建时默认状态为 DRAFT（草稿），创建后返回完整的简历详情（含级联的模块和条目）。
     */
    @Override
    @Transactional
    public Resume createResume(CreateResumeDto dto) {
        Resume resume = new Resume();
        resume.setUserId(Long.valueOf(BaseContext.getCurrentId()));
        resume.setName(dto.getName());
        resume.setTemplateType(dto.getTemplateType());
        resume.setIsDefault(dto.getIsDefault());
        resume.setStatus("DRAFT");
        resumeMapper.insert(resume);
        // 插入后通过 selectDetailById 获取含级联数据的完整对象
        return resumeMapper.selectDetailById(resume.getId());
    }

    /**
     * 获取简历完整详情
     * 通过 ResumeMapper.xml 中定义的 resumeDetailMap 进行三级嵌套查询：
     * resume -> resume_section -> resume_item
     */
    @Override
    public Resume getResumeDetail(Long resumeId) {
        Resume resume = resumeMapper.selectDetailById(resumeId);
        if (resume == null) {
            throw new UserException(ExceptionCommon.USER_NOT_EXIST);
        }
        return resume;
    }

    /**
     * 更新简历基本信息
     * 只用非空字段更新，支持部分更新。
     */
    @Override
    public void updateResume(Long resumeId, UpdateResumeDto dto) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new UserException(ExceptionCommon.USER_NOT_EXIST);
        }
        Resume update = new Resume();
        update.setId(resumeId);
        update.setName(dto.getName());
        update.setTemplateType(dto.getTemplateType());
        update.setIsDefault(dto.getIsDefault());
        update.setStatus(dto.getStatus());
        update.setAiSummary(dto.getAiSummary());
        resumeMapper.update(update);
    }

    /**
     * 删除整份简历（级联删除）
     * 删除顺序：条目 -> 模块 -> 简历，确保外键约束不被违反。
     * 使用 @Transactional 保证三个删除操作在同一事务中。
     */
    @Override
    @Transactional
    public void deleteResume(Long resumeId) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new UserException(ExceptionCommon.USER_NOT_EXIST);
        }
        // 先删条目（通过子查询关联到 resume_id）
        resumeItemMapper.deleteByResumeId(resumeId);
        // 再删模块
        resumeSectionMapper.deleteByResumeId(resumeId);
        // 最后删简历主表
        resumeMapper.deleteById(resumeId);
    }

    /**
     * 获取用户的所有简历列表
     * 此接口只返回简历基本信息，不包含模块和条目详情（列表页不需要完整数据）。
     */
    @Override
    public Object listUserResumes() {
        return resumeMapper.selectByUserId(Long.valueOf(BaseContext.getCurrentId()));
    }

    /**
     * 给简历添加一个新模块
     * 先验证简历存在，创建模块时默认 visible=1（可见）。
     */
    @Override
    @Transactional
    public Long addSection(Long resumeId, CreateSectionDto dto) {
        Resume resume = resumeMapper.selectById(resumeId);
        if (resume == null) {
            throw new UserException(ExceptionCommon.USER_NOT_EXIST);
        }
        ResumeSection section = new ResumeSection();
        section.setResumeId(resumeId);
        section.setSectionType(dto.getSectionType());
        section.setCustomLabel(dto.getCustomLabel());
        section.setSortOrder(dto.getSortOrder());
        section.setVisible(1);
        resumeSectionMapper.insert(section);
        return section.getId();
    }

    /**
     * 更新模块信息
     * 校验模块存在且属于指定的简历，防止跨简历篡改。
     */
    @Override
    public void updateSection(Long resumeId, Long sectionId, UpdateSectionDto dto) {
        ResumeSection section = resumeSectionMapper.selectById(sectionId);
        if (section == null || !section.getResumeId().equals(resumeId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        ResumeSection update = new ResumeSection();
        update.setId(sectionId);
        update.setSectionType(dto.getSectionType());
        update.setCustomLabel(dto.getCustomLabel());
        update.setSortOrder(dto.getSortOrder());
        update.setVisible(dto.getVisible());
        resumeSectionMapper.update(update);
    }

    /**
     * 删除模块（级联删除其下所有条目）
     * 先删模块下的所有条目，再删模块本身。
     */
    @Override
    @Transactional
    public void deleteSection(Long resumeId, Long sectionId) {
        ResumeSection section = resumeSectionMapper.selectById(sectionId);
        if (section == null || !section.getResumeId().equals(resumeId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        resumeItemMapper.deleteBySectionId(sectionId);
        resumeSectionMapper.deleteById(sectionId);
    }

    /**
     * 在指定模块下添加一个条目
     * content 字段为 JSON 字符串，前端负责将结构化数据序列化为JSON传入。
     */
    @Override
    @Transactional
    public Long addItem(Long resumeId, Long sectionId, CreateItemDto dto) {
        ResumeSection section = resumeSectionMapper.selectById(sectionId);
        if (section == null || !section.getResumeId().equals(resumeId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        ResumeItem item = new ResumeItem();
        item.setSectionId(sectionId);
        item.setContent(dto.getContent());
        item.setSortOrder(dto.getSortOrder());
        resumeItemMapper.insert(item);
        return item.getId();
    }

    /**
     * 更新条目内容
     * 校验条目存在且属于指定的模块，并验证模块属于指定简历。
     */
    @Override
    public void updateItem(Long resumeId, Long sectionId, Long itemId, UpdateItemDto dto) {
        ResumeSection section = resumeSectionMapper.selectById(sectionId);
        if (section == null || !section.getResumeId().equals(resumeId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        ResumeItem item = resumeItemMapper.selectById(itemId);
        if (item == null || !item.getSectionId().equals(sectionId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        ResumeItem update = new ResumeItem();
        update.setId(itemId);
        update.setContent(dto.getContent());
        update.setSortOrder(dto.getSortOrder());
        resumeItemMapper.update(update);
    }

    /**
     * 删除单个条目
     * 校验条目存在且属于指定的模块，并验证模块属于指定简历。
     */
    @Override
    @Transactional
    public void deleteItem(Long resumeId, Long sectionId, Long itemId) {
        ResumeSection section = resumeSectionMapper.selectById(sectionId);
        if (section == null || !section.getResumeId().equals(resumeId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        ResumeItem item = resumeItemMapper.selectById(itemId);
        if (item == null || !item.getSectionId().equals(sectionId)) {
            throw new UserException(ExceptionCommon.PARAM_ERROR);
        }
        resumeItemMapper.deleteById(itemId);
    }
}
