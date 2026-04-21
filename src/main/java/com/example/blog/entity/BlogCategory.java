package com.example.blog.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data

public class BlogCategory {

    private Integer categoryId;//分类id

    private String categoryName;//分类名称

    private String categoryIcon;//分类图标

    private Integer categoryRank;//分类排序序号

    private Byte isDeleted;//删除标识字段(0-未删除 1-已删除)

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;//创建时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

}
