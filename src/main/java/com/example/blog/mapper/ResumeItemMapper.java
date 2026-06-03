package com.example.blog.mapper;

import com.example.blog.entity.ResumeItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 模块条目 Mapper 接口
 * 提供条目表的增删改查操作，以及按模块ID和简历ID的批量删除。
 * 其中 deleteByResumeId 通过子查询关联到简历级别，用于级联删除。
 * SQL 定义在 resources/mapper/ResumeItemMapper.xml 中。
 */
@Mapper
public interface ResumeItemMapper {

    /** 新增条目 */
    void insert(ResumeItem item);

    /** 根据ID查询单个条目 */
    ResumeItem selectById(Long id);

    /** 查询某模块下的所有条目 */
    List<ResumeItem> selectBySectionId(Long sectionId);

    /** 更新条目内容 */
    void update(ResumeItem item);

    /** 删除单个条目 */
    void deleteById(Long id);

    /** 删除某模块下的所有条目（用于级联删除模块时） */
    void deleteBySectionId(Long sectionId);

    /** 删除某简历下的所有条目（用于级联删除简历时） */
    void deleteByResumeId(Long resumeId);
}
