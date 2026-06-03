package com.example.blog.model.dto.resume;

import jakarta.validation.constraints.NotBlank;

/**
 * 创建简历请求DTO
 * 前端创建新简历时传递的参数。
 * userId 由后端从 BaseContext 自动获取，前端无需传递。
 * name 为必填，templateType 和 isDefault 可选。
 */
public class CreateResumeDto {

    /** 简历名称，如"2024秋招版"，不能为空 */
    @NotBlank(message = "简历名称不能为空")
    private String name;

    /** 模板类型，如 MODERN/CLASSIC/MINIMAL，为空则使用默认模板 */
    private String templateType;

    /** 是否设置为默认简历：0-否，1-是 */
    private Integer isDefault;

    @NotBlank(message = "简历名称不能为空")
    public String getName() { return name; }
    public void setName(@NotBlank(message = "简历名称不能为空") String name) { this.name = name; }

    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }

    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }
}
