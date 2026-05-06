package com.example.blog.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Admin {


    private Long adminId;
    @NotBlank(message = "用户名不能为空")//添加数据时，不能为空，自动去除空格
    private String adminName;
    @NotBlank(message = "密码不能为空")
    private String adminPassword;
    @NotBlank(message = "用户账号不能为空")
    private String adminAccount;
    private int adminStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;//创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;//修改时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date exitTime;//退出时间

    @Email(message = "邮箱格式不正确")
    private String email;//邮箱

    @NotBlank(message = "角色不能为空")
    private String role;
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
//    private Date registerTime;//注册时间


}
