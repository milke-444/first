package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/** 博客评论实体类*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogComment {
    private Long commentId;//评论id
    private Long blogId;//博客id
    private Long adminId;

    private String commentBody;//评论内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String commentCreateTime;//评论创建时间
    private Byte isDleted ;//是否删除0-未删除 1-已删除
    private Long parentId;//父评论id
    private String replyBody;//回复内容
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date replyCreateTime;//回复创建时间
    private Byte commentStatus;//评论状态：0-审核中；1-审核通过





}
