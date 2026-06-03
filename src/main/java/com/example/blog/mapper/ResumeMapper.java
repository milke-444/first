package com.example.blog.mapper;

import com.example.blog.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 简历 Mapper 接口
 * 提供简历表的增删改查操作，以及带级联数据的详情查询。
 * SQL 定义在 resources/mapper/ResumeMapper.xml 中。
 */
@Mapper
public interface ResumeMapper {

    /** 新增简历，插入后自动回填主键到实体 */
    void insert(Resume resume);

    /** 查询简历详情（含所有模块和条目），使用 resumeDetailMap 嵌套映射 */
    Resume selectDetailById(Long id);

    /** 查询简历基本信息（不含模块条目） */
    Resume selectById(Long id);

    /** 查询某用户的所有简历列表 */
    List<Resume> selectByUserId(Long userId);

    /** 更新简历信息，动态SQL只更新非空字段 */
    void update(Resume resume);

    /** 物理删除简历 */
    void deleteById(Long id);
}
