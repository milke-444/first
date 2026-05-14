package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNameDto {
    @NotBlank(message = "用户名称不能为空")
    private String oldName;
    @NotBlank(message = "用户名称不能为空")
    private String newName;
}
