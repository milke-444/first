package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * 简历主表实体类
 * 对应数据库 resume 表，存储一份简历的核心信息。
 * 包含简历名称、模板类型、状态等基本信息，以及关联的模块列表。
 */
public class Resume {
    /** 主键ID */
    private Long id;
    /** 用户ID，关联用户表 */
    private Long userId;
    /** 简历名称，如"我的前端简历"、"2024秋招版" */
    private String name;
    /** 模板类型，如 MODERN/CLASSIC/MINIMAL */
    private String templateType;
    /** 是否默认简历：0-否，1-是 */
    private Integer isDefault;
    /** 简历状态：DRAFT-草稿，COMPLETE-已完成 */
    private String status;
    /** AI生成的简历总结/评语 */
    private String aiSummary;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;
    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updatedAt;

    /** 非数据库字段：简历包含的模块列表（含条目），由 MyBatis collection 嵌套查询填充 */
    private List<ResumeSection> sections;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    public List<ResumeSection> getSections() { return sections; }
    public void setSections(List<ResumeSection> sections) { this.sections = sections; }
}
