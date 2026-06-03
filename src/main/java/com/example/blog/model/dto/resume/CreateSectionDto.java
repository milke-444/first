package com.example.blog.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 添加简历模块请求DTO
 * 前端给简历添加新模块时传递的参数。
 * sectionType 和 sortOrder 为必填。
 * 当 sectionType=OBJECT 时，customLabel 用于存储用户自定义的模块名（如"证书"、"兴趣爱好"）。
 */
public class CreateSectionDto {

    /** 模块类型：EDUCATION/WORK/PROJECT/SKILL/OBJECT */
    @NotBlank(message = "模块类型不能为空")
    private String sectionType;

    /** 自定义模块名称（仅在 sectionType=OBJECT 时使用） */
    private String customLabel;

    /** 排序序号，决定模块在简历中的显示顺序 */
    @NotNull(message = "排序序号不能为空")
    private Integer sortOrder;

    @NotBlank(message = "模块类型不能为空")
    public String getSectionType() { return sectionType; }
    public void setSectionType(@NotBlank(message = "模块类型不能为空") String sectionType) { this.sectionType = sectionType; }

    public String getCustomLabel() { return customLabel; }
    public void setCustomLabel(String customLabel) { this.customLabel = customLabel; }

    @NotNull(message = "排序序号不能为空")
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(@NotNull(message = "排序序号不能为空") Integer sortOrder) { this.sortOrder = sortOrder; }
}
