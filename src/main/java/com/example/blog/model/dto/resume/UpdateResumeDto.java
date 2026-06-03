package com.example.blog.model.dto.resume;

/**
 * 更新简历基本信息请求DTO
 * 所有字段均为可选，只传需要修改的字段即可。
 * 使用 MyBatis 的 <set> + <if> 动态SQL只更新非空字段。
 */
public class UpdateResumeDto {

    /** 简历名称 */
    private String name;
    /** 模板类型 */
    private String templateType;
    /** 是否默认简历 */
    private Integer isDefault;
    /** 简历状态：DRAFT-草稿，COMPLETE-已完成 */
    private String status;
    /** AI总结/评语 */
    private String aiSummary;

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
}
