package com.example.blog.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCategoryDto {
    @NotNull(message = "分类id不能为空")
    @Min(value = 1, message = "分类id不能小于1")
    private Integer categoryId;//分类id

    @NotBlank(message = "分类名称不能为空")
    @Size(min = 2, max = 100, message = "分类名称长度必须在 {min} 到 {max} 之间")
    private String categoryName;//分类名称

    @NotBlank(message = "分类图标不能为空")
    private String categoryIcon;//分类图标

    @NotNull(message = "分类排序序号不能为空")
    @Min(value = 1, message = "分类排序序号不能小于1")
    private Integer categoryRank;//分类排序序号


}
