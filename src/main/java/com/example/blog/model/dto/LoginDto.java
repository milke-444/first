package com.example.blog.model.dto;

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

    public @NotBlank(message = "用户名不能为空") String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(@NotBlank(message = "用户名不能为空") String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public @NotBlank(message = "密码不能为空") String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(@NotBlank(message = "密码不能为空") String adminPassword) {
        this.adminPassword = adminPassword;
    }
}
