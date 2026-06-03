package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * 简历模块表实体类
 * 对应 resume_section 表，代表简历中的一个功能模块。
 * 模块类型包括：EDUCATION(教育背景)、WORK(工作经历)、PROJECT(项目经验)、
 * SKILL(技能特长)、OBJECT(自定义模块)。
 * 当 sectionType=OBJECT 时，customLabel 存储用户自定义的模块名称。
 */
public class ResumeSection {
    /** 主键ID */
    private Long id;
    /** 关联 resume 表的主键 */
    private Long resumeId;
    /** 模块类型：EDUCATION/WORK/PROJECT/SKILL/OBJECT */
    private String sectionType;
    /** 当 sectionType=OBJECT 时，用户自定义的模块名 */
    private String customLabel;
    /** 排序序号，升序排列 */
    private Integer sortOrder;
    /** 是否可见：0-隐藏，1-可见 */
    private Integer visible;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    /** 非数据库字段：该模块下的条目列表，由 MyBatis collection 嵌套查询填充 */
    private List<ResumeItem> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getResumeId() { return resumeId; }
    public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
    public String getSectionType() { return sectionType; }
    public void setSectionType(String sectionType) { this.sectionType = sectionType; }
    public String getCustomLabel() { return customLabel; }
    public void setCustomLabel(String customLabel) { this.customLabel = customLabel; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public List<ResumeItem> getItems() { return items; }
    public void setItems(List<ResumeItem> items) { this.items = items; }
}
