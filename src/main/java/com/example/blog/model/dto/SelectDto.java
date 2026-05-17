package com.example.blog.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectDto {

    @NotBlank(message = "用户账号不能为空")
    private String adminAccount;
    @NotBlank(message = "用户名称不能为空")
    private String adminName;

    public @NotBlank(message = "用户账号不能为空") String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(@NotBlank(message = "用户账号不能为空") String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public @NotBlank(message = "用户名称不能为空") String getAdminName() {
        return adminName;
    }

    public void setAdminName(@NotBlank(message = "用户名称不能为空") String adminName) {
        this.adminName = adminName;
    }
}
