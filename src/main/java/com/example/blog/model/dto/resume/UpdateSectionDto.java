package com.example.blog.model.dto.resume;

/**
 * 更新简历模块请求DTO
 * 所有字段均为可选，只传需要修改的字段。
 * visible 字段控制模块是否在前端展示（0=隐藏，1=可见）。
 */
public class UpdateSectionDto {

    /** 模块类型 */
    private String sectionType;
    /** 自定义模块名 */
    private String customLabel;
    /** 排序序号 */
    private Integer sortOrder;
    /** 是否可见：0-隐藏，1-可见 */
    private Integer visible;

    public String getSectionType() { return sectionType; }
    public void setSectionType(String sectionType) { this.sectionType = sectionType; }
    public String getCustomLabel() { return customLabel; }
    public void setCustomLabel(String customLabel) { this.customLabel = customLabel; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getVisible() { return visible; }
    public void setVisible(Integer visible) { this.visible = visible; }
}
