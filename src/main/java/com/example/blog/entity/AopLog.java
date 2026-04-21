package com.example.blog.entity;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AopLog {
   private Integer aopId;//主键id
   private Integer userId;//用户名
    private String userName;
   private LocalDateTime createTime;//创建时间
    private String className;//类名
    private String methodName;//方法名
    private String description;
    /** 操作方法全路径（如：com.example.service.UserService.addUser） */
    private String method;// 方法参数
    private String params;
    /** 操作结果（成功/失败，JSON格式） */
    private Object result;
    /** 异常信息（失败时记录） */
    private String exception;
    /** 操作耗时（毫秒） */
    private Long costTime;
}
