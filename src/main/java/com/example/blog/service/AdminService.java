package com.example.blog.service;

import com.example.blog.model.dto.LoginDto;
import com.example.blog.entity.Admin;
import com.example.blog.model.dto.ResigerDto;
import com.example.blog.model.vo.LoginVo;

public interface AdminService {
    LoginVo adminlogin(LoginDto loginDto);

    void adminRegister(ResigerDto admin);


    Admin getById();


   Admin updatePassword(String newPassword, String oldPassword);


    Admin updateName(Integer currentId, String newName, String oldName);
}
