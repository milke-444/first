package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    public @NotBlank(message = "旧密码不能为空") String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(@NotBlank(message = "旧密码不能为空") String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public @NotBlank(message = "新密码不能为空") String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(@NotBlank(message = "新密码不能为空") String newPassword) {
        this.newPassword = newPassword;
    }
}
