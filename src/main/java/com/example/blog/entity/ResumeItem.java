package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 模块条目表实体类
 * 对应 resume_item 表，存储简历模块下的具体条目内容。
 * content 字段为 JSON 格式，可以灵活存储不同类型模块的结构化数据。
 * 例如：
 *   教育模块：{"school":"xx大学","major":"计算机","degree":"本科","startDate":"2020-09","endDate":"2024-06"}
 *   工作模块：{"company":"xx公司","position":"后端开发","startDate":"2024-07","description":"负责xx系统"}
 *   技能模块：{"skillName":"Java","proficiency":"精通"}
 */
public class ResumeItem {
    /** 主键ID */
    private Long id;
    /** 关联 resume_section 表的主键 */
    private Long sectionId;
    /** 条目内容（JSON格式），灵活存储结构化数据 */
    private String content;
    /** 排序序号，升序排列 */
    private Integer sortOrder;
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSectionId() { return sectionId; }
    public void setSectionId(Long sectionId) { this.sectionId = sectionId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
