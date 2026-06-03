package com.example.blog.model.dto.resume;

/**
 * 更新模块条目请求DTO
 * 所有字段均为可选，只传需要修改的字段即可。
 */
public class UpdateItemDto {

    /** 条目内容（JSON字符串） */
    private String content;
    /** 排序序号 */
    private Integer sortOrder;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
