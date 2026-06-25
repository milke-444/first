package com.example.blog.model.vo.common;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ExceptionCommon {
    // 自定义异常枚举，使用枚举类型，让所有异常信息都在ExceptionCommon中定义，方便后续使用,普通常量，没有安全限制，使用错了也不报错
    //保证数据只能是枚举中的值，避免数据错误

    UNKNOWN_ERROR(500, "未知错误"),
    PARAM_ERROR(400, "参数错误"),
    USER_NOT_EXIST(404, "用户不存在"),
    USER_EXIST(400, "用户已存在"),
    USER_PASSWORD_ERROR(400, "用户密码错误"),
    USER_NOT_LOGIN(401, "用户未登录"),
    USER_NOT_ADMIN(403, "用户无权限"),
    ;
    private Integer code;
    private String message;

    private ExceptionCommon(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
