package com.example.blog.common.globalException;

import com.example.blog.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * 全局异常处理,接收service层的异常，如何没抛则输出自定义的异常信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();//打印异常信息
        return Result.failure(StringUtils.hasLength(e.getMessage())? e.getMessage():"服务器异常");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)//设置异常类型为400
    @ExceptionHandler({MethodArgumentNotValidException.class})//捕获@valid注解验证参数验证异常
    public Result handleException(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();//获取BindingResult对象，它可以获得异常中的参数验证错误信息
//        log.info("参数验证异常:{}",bindingResult.getAllErrors());//记录日志
        //判断BindingResult对象是否包含错误信息
        if (bindingResult.hasErrors()){
            List<ObjectError> errors = bindingResult.getAllErrors();//获取错误信息，封装为List对象
            //集合不为空，说明有异常存在
            if (!errors.isEmpty()){
                ObjectError error = errors.get(0);//获取第一个错误信息
                return Result.failure(error.getDefaultMessage()
                        );
            }
        }
        return Result.failure("参数验证异常");


    }

    @ExceptionHandler(UserException.class)
    public Result handleUserException(UserException e) {
        e.printStackTrace();
        if (StringUtils.isEmpty(e.getMessage())) {
            return Result.failure("未知错误");
        }
        return Result.error(e.getMessage(), e.getCode());
    }
}
