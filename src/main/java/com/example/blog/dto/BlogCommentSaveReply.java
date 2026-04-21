package com.example.blog.dto;

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
}
