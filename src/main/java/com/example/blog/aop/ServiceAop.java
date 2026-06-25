package com.example.blog.aop;

import com.alibaba.fastjson.JSON;
import com.example.blog.common.context.BaseContext;
import com.example.blog.entity.AopLog;
import com.example.blog.service.AopStorage;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class ServiceAop {
    /**
     * 优化日志的存储，可以存储用户名和参数
     * 优化慢查询
     * 优化日志的提示，更清晰
     *
     * */
    @Autowired
    private AopStorage aopStorage;
    /**
     * 切点：排除 AopStorageImpl（自身）和 PdfExportService（参数含 HttpServletResponse，无法 JSON 序列化）
     */
    @Pointcut("execution(* com.example.blog.service.impl.*.*(..)) " +
              "&& !target(com.example.blog.service.impl.AopStorageImpl) " +
              "&& !target(com.example.blog.service.impl.PdfExportService)")
    public void ServiceAop(){}

    @Around("ServiceAop()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        AopLog aop = new AopLog();//创建一个aop对象
        //获取执行的开始时间
        Long start = System.currentTimeMillis();
        //获取类名
        String className = joinPoint.getTarget().getClass().getSimpleName();//类名
        //获取方法名
        String methodName = joinPoint.getSignature().getName();//方法名
        //获取方法全路径
        String fullMethodName = joinPoint.getSignature().toLongString();//方法全路径
        //获取参数
        Object[] args = joinPoint.getArgs();
        //设置参数
        aop.setClassName( className);
        aop.setMethodName(methodName);
        aop.setCostTime(System.currentTimeMillis() - start);
        aop.setDescription(fullMethodName);
        aop.setUserId(BaseContext.getCurrentId());
        String params;
        try {
            params = JSON.toJSONString(args);
        } catch (Exception e) {
            params = "[序列化参数失败: " + e.getMessage() + "]";
//            log.warn("AOP参数序列化失败, className={}, methodName={}", className, methodName, e);
        }
        aop.setParams(params);
        //初始化执行方法
        Object result = null;
        try {
            result = joinPoint.proceed();
//            log.info("Service执行成功" + "执行类" + className + "执行方法" + methodName);
            aop.setResult("成功");
            return result;


        } catch (Exception e) {
//            log.error("Service执行失败" + "执行类" + className + "执行方法" + methodName);
            aop.setResult("失败");
            aop.setException(e.getMessage());
            throw e;

        } finally {
            Long end = System.currentTimeMillis();
            aop.setCostTime(end - start);
//            log.info("Service执行结束" + "总耗时" + (end - start) + "毫秒");
            aopStorage.save(aop);

        }

    }
    }



