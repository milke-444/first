package com.example.blog.mapper;
import com.example.blog.entity.Admin;
import com.example.blog.vo.LoginVo;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface AdminMapper {
    @Select("select * from admin where admin_account=#{adminAccount} and admin_password=#{adminPassword}")
    Admin adminlogin(String adminAccount, String adminPassword);

//    @Insert("insert into admin(admin_name ,admin_account,admin_password) values(#{adminAccount},#{adminPassword}, #{adminName})")
    void adminRegister(Admin admin);

    @Select("select * from admin where admin_id=#{adminId}")
    Admin getById(Integer adminId);


    @Select("select admin_password from admin where admin_id= #{currentId}")
    String selectPassword(Integer currentId);

    @Update("update admin set admin_password=#{newPassword} where admin_id=#{currentId}")
    void updatePassword(int currentId, String newPassword);

    @Update("update admin set admin_name= #{newName} where admin_id= #{currentId}")
    void updateName(Integer currentId, String newName);


}
