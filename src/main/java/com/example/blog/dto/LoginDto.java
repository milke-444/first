package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginDto {
    @NotBlank(message = "用户名不能为空")
    private String adminAccount;
    @NotBlank(message = "密码不能为空")
    private String adminPassword;
}
