package com.example.blog.common.globalException;

import com.example.blog.model.vo.common.ExceptionCommon;
import lombok.Data;

@Data
public class UserException extends RuntimeException{
   private String message;//异常信息
   private Integer code;//错误码
   private ExceptionCommon exceptionCommon;//错误类型
    public UserException(ExceptionCommon exceptionCommon){
        this.exceptionCommon = exceptionCommon;
        this.message = exceptionCommon.getMessage();
        this.code = exceptionCommon.getCode();
    }
    public UserException(String message,Integer code){
        this.message = message;
        this.code = code;

    }
}
