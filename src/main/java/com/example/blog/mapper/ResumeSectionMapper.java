package com.example.blog.mapper;

import com.example.blog.entity.ResumeSection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 简历模块 Mapper 接口
 * 提供模块表的增删改查操作。
 * 其中 selectByResumeId 会级联查询该模块下的所有条目。
 * SQL 定义在 resources/mapper/ResumeSectionMapper.xml 中。
 */
@Mapper
public interface ResumeSectionMapper {

    /** 新增模块 */
    void insert(ResumeSection section);

    /** 根据ID查询单个模块 */
    ResumeSection selectById(Long id);

    /** 查询某简历下的所有模块（含每个模块下的条目） */
    List<ResumeSection> selectByResumeId(Long resumeId);

    /** 更新模块信息 */
    void update(ResumeSection section);

    /** 删除单个模块 */
    void deleteById(Long id);

    /** 删除某简历下的所有模块（用于级联删除） */
    void deleteByResumeId(Long resumeId);
}
