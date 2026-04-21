package com.example.blog.service;

import com.example.blog.dto.LoginDto;
import com.example.blog.entity.Admin;
import com.example.blog.result.Result;
import com.example.blog.vo.LoginVo;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;

public interface AdminService {
    LoginVo adminlogin(LoginDto loginDto);

    void adminRegister(Admin admin);


    Admin getById();


   Admin updatePassword(String newPassword, String oldPassword);


    Admin updateName(Integer currentId, String newName, String oldName);
}
