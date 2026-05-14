package com.example.blog.common.result;


import com.example.blog.common.constant.ResultConstant;
import lombok.AccessLevel;  // 用于控制构造方法的访问级别
import lombok.Builder;  // 用于生成构建器模式的代码
import lombok.Getter;  // 用于生成属性的Getter方法

/**
 * 所有给前端返回的响应数据的统一模型
 * 统一并自定义返回状态码，如有需求可以另外增加
 * @author YuYan
 * @date 2024-10-28 15:07:17
 *
 * 主要作用：用于封装后端向前端返回的响应数据，通过统一的响应格式
 * 通过状态码和消息来表示操作的结果，并可携带响应数据
 */
// @Builder：使用lombok生成一个私有的构造器，方便创建Result对象
@Builder(access = AccessLevel.PRIVATE)
// @Getter：使用lombok为类的所有字段生成Getter方法
@Getter
public class Result {


    private Integer code;//状态码
    // 表示响应消息
    private String message;
    // 响应数据
    private Object data;

    // 静态方法：请求成功
    public static Result success() {
        return success(ResultConstant.DEFAULT_SUCCESS_MESSAGE);
    }
    // 请求成功，指定自定义消息
    public static Result success(String message) {
        return success(message, null);
    }
    // 请求成功，默认消息为“操作成功”
    public static Result success(Object data) {
        return success(ResultConstant.DEFAULT_SUCCESS_MESSAGE, data);
    }
    // 请求成功，返回一个成功的Result对象，指定自定义消息和响应数据
    public static Result success(String message, Object data) {
        return Result.builder()
                .code(ResultConstant.SUCCESS)
                .message(message)
                .data(data)
                .build();
    }

    // 请求失败
    public static Result failure(String defaultFailureMessage) {
        return failure(ResultConstant.DEFAULT_FAILURE_MESSAGE);
    }
    // 返回一个失败的Result对象，指定自定义消息
    public static Result error(String message, Integer code) {
        return Result.builder()
                .code(ResultConstant.FAILURE)
                .message(message)
                .build();
    }

    /**
     * 该 Result 类定义了一个统一的响应格式，用于封装后端返回给前端的数据。通过状态码、操作信息和
     * 响应数据的组合，确保所有接口返回的数据格式一致，便于前端解析和处理。
     * Result类可以应用于任何需要统一响应格式的后端服务中，特别是在RESTful API开发中
     *
     * 常见的应用场景包括：
     * 1、用户登录：返回登录或失败的信息
     * 2、数据查询：返回查询结果的状态信息
     * 3、数据修改：返回修改操作的结果和状态信息
     * 4、文件上传：返回上传结果和状态信息
     */

    /**
     * RESTful API是一种基于HTTP协议的Web服务接口设计风格。REST（Representation State Transfer）
     * 代表 ”表现层状态转换“，其核心思想是通过标准的HTTP方法（如GET、POST、PUT、DELETE等）对
     * 资源进行操作。RESTful API设计的目标是使Web服务更加简单、可扩展和易于理解。
     *
     */

}
