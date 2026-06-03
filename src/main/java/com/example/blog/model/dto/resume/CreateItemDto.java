package com.example.blog.model.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 添加模块条目请求DTO
 * 前端给某个模块添加一条内容时传递的参数。
 * content 为JSON格式的字符串，灵活存储不同模块类型的结构化数据。
 * sortOrder 决定条目在模块内的显示顺序。
 */
public class CreateItemDto {

    /** 条目内容（JSON字符串），如：{"company":"xx公司","position":"后端开发"} */
    @NotBlank(message = "条目内容不能为空")
    private String content;

    /** 排序序号 */
    @NotNull(message = "排序序号不能为空")
    private Integer sortOrder;

    @NotBlank(message = "条目内容不能为空")
    public String getContent() { return content; }
    public void setContent(@NotBlank(message = "条目内容不能为空") String content) { this.content = content; }

    @NotNull(message = "排序序号不能为空")
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(@NotNull(message = "排序序号不能为空") Integer sortOrder) { this.sortOrder = sortOrder; }
}
