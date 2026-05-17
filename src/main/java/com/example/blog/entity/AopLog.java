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

 public Integer getAopId() {
  return aopId;
 }

 public void setAopId(Integer aopId) {
  this.aopId = aopId;
 }

 public Integer getUserId() {
  return userId;
 }

 public void setUserId(Integer userId) {
  this.userId = userId;
 }

 public String getUserName() {
  return userName;
 }

 public void setUserName(String userName) {
  this.userName = userName;
 }

 public LocalDateTime getCreateTime() {
  return createTime;
 }

 public void setCreateTime(LocalDateTime createTime) {
  this.createTime = createTime;
 }

 public String getClassName() {
  return className;
 }

 public void setClassName(String className) {
  this.className = className;
 }

 public String getMethodName() {
  return methodName;
 }

 public void setMethodName(String methodName) {
  this.methodName = methodName;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }

 public String getMethod() {
  return method;
 }

 public void setMethod(String method) {
  this.method = method;
 }

 public String getParams() {
  return params;
 }

 public void setParams(String params) {
  this.params = params;
 }

 public Object getResult() {
  return result;
 }

 public void setResult(Object result) {
  this.result = result;
 }

 public String getException() {
  return exception;
 }

 public void setException(String exception) {
  this.exception = exception;
 }

 public Long getCostTime() {
  return costTime;
 }

 public void setCostTime(Long costTime) {
  this.costTime = costTime;
 }
}
