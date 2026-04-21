package com.example.blog.mapper;

import com.example.blog.entity.AopLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AopLogMapper {
    @Insert("insert into aop_log(user_id,user_name,class_name,method_name,description,params,result,exception,cost_time) values(#{userId},#{userName},#{className},#{methodName},#{description},#{params},#{result},#{exception},#{costTime})")
    void save(AopLog aop);

    @Select("select admin_name from admin where admin_id=#{userId}")
    String nameselect(Integer userId);
}
