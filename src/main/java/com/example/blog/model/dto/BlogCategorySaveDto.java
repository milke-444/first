package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCategorySaveDto {
    @NotBlank(message = "分类名称不能为空")
    @Size(min = 2, max = 20, message = "分类名称长度必须在 {min} 到 {max} 之间")
    private String categoryName;//分类名称

    @NotBlank(message = "分类图标不能为空")
    private String categoryIcon;//分类图标

    public @NotBlank(message = "分类名称不能为空") @Size(min = 2, max = 20, message = "分类名称长度必须在 {min} 到 {max} 之间") String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(@NotBlank(message = "分类名称不能为空") @Size(min = 2, max = 20, message = "分类名称长度必须在 {min} 到 {max} 之间") String categoryName) {
        this.categoryName = categoryName;
    }

    public @NotBlank(message = "分类图标不能为空") String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(@NotBlank(message = "分类图标不能为空") String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }
}
