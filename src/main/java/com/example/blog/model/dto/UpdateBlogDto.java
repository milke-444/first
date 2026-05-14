package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBlogDto {
    @NotNull(message = "博客id不能为空")
    private Long blogId;

    @NotBlank(message = "博客标题不能为空")
    @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
    private String blogName;

    @NotBlank(message = "博客作者不能为空")
    @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
    private String blogGgeer;

    @NotBlank(message = "博客内容不能为空")
    @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
    private String blogContent;

    @NotBlank(message = "博客封面不能为空")
    private String blogCoverImage;


    private String blogCategoryName;

    @NotBlank(message = "博客标签不能为空")
    @Size(min = 2, max = 100, message = "博客标题长度必须在 {min} 到 {max} 之间")
    private String blogTags;  // 标签可为空

    private Integer blogCategoryId;//博客分类id

}
