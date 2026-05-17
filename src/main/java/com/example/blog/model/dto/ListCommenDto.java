package com.example.blog.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListCommenDto {
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码不能小于1")
    private Integer page;
    @NotNull(message = "页大小不能为空")
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能大于100")
    private Integer pageSize;
    @NotNull(message = "博客id不能为空")
    private Long blogId;

    public @NotNull(message = "页码不能为空") @Min(value = 1, message = "页码不能小于1") Integer getPage() {
        return page;
    }

    public void setPage(@NotNull(message = "页码不能为空") @Min(value = 1, message = "页码不能小于1") Integer page) {
        this.page = page;
    }

    public @NotNull(message = "页大小不能为空") @Min(value = 1, message = "页大小不能小于1") @Max(value = 100, message = "页大小不能大于100") Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(@NotNull(message = "页大小不能为空") @Min(value = 1, message = "页大小不能小于1") @Max(value = 100, message = "页大小不能大于100") Integer pageSize) {
        this.pageSize = pageSize;
    }

    public @NotNull(message = "博客id不能为空") Long getBlogId() {
        return blogId;
    }

    public void setBlogId(@NotNull(message = "博客id不能为空") Long blogId) {
        this.blogId = blogId;
    }

    public Integer getStart() {
        return (page - 1) * pageSize;
    }
}
