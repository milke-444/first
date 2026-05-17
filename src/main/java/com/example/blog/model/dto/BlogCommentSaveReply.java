package com.example.blog.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BlogCommentSaveReply {

    @NotNull(message = "回复内容不能为空")
    private String replyBody;//回复内容
    private Long adminId;
    @NotNull(message = "博客id不能为空")
    private Long blogId;//博客id
    @NotNull(message = "评论id不能为空")
    private Byte parentId;//父评论id

    public @NotNull(message = "回复内容不能为空") String getReplyBody() {
        return replyBody;
    }

    public void setReplyBody(@NotNull(message = "回复内容不能为空") String replyBody) {
        this.replyBody = replyBody;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public @NotNull(message = "博客id不能为空") Long getBlogId() {
        return blogId;
    }

    public void setBlogId(@NotNull(message = "博客id不能为空") Long blogId) {
        this.blogId = blogId;
    }

    public @NotNull(message = "评论id不能为空") Byte getParentId() {
        return parentId;
    }

    public void setParentId(@NotNull(message = "评论id不能为空") Byte parentId) {
        this.parentId = parentId;
    }
}
