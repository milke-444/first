package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class Blog {
    private Long blogId;//博客id
    private String blogName;//博客标题
    private String blogGgeer;//博客作者
    private String blogContent;//博客内容
    private Byte blogStatus;//博客状态
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date blogCreateTime;//博客创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date blogUpdateTime;//博客更新时间
    private String blogCoverImage;//博客封面
    private String blogCategoryName;//博客分类名称
    private String blogTags;//博客标签
    private Long blogViews;//博客浏览量
    private Byte enableComment;//是否允许评论
    private Byte isDeleted;//是否删除
    private Integer blogCategoryId;//博客分类id
    private Integer likeCount;//点赞数

}
