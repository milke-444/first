package com.example.blog.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public class ResigerDto {
    @NotBlank(message = "用户名不能为空")//添加数据时，不能为空，自动去除空格
    private String adminName;
    @NotBlank(message = "密码不能为空")
    private String adminPassword;
    @NotBlank(message = "用户账号不能为空")
    private String adminAccount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;//修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exitTime;//退出时间

    public @NotBlank(message = "用户名不能为空") String getAdminName() {
        return adminName;
    }

    public void setAdminName(@NotBlank(message = "用户名不能为空") String adminName) {
        this.adminName = adminName;
    }

    public @NotBlank(message = "密码不能为空") String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(@NotBlank(message = "密码不能为空") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public @NotBlank(message = "用户账号不能为空") String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(@NotBlank(message = "用户账号不能为空") String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
}
