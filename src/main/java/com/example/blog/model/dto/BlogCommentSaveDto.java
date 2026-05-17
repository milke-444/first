package com.example.blog.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogCommentSaveDto {
    @NotNull(message = "博客id不能为空")
    private Long blogId;//博客id
    @NotNull(message = "评论内容不能为空")
    private String commentBody;//评论内容
    private Long adminId;

    public @NotNull(message = "博客id不能为空") Long getBlogId() {
        return blogId;
    }

    public void setBlogId(@NotNull(message = "博客id不能为空") Long blogId) {
        this.blogId = blogId;
    }

    public @NotNull(message = "评论内容不能为空") String getCommentBody() {
        return commentBody;
    }

    public void setCommentBody(@NotNull(message = "评论内容不能为空") String commentBody) {
        this.commentBody = commentBody;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }
}
