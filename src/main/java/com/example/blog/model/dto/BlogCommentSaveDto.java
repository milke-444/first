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
}
