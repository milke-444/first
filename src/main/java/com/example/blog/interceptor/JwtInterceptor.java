package com.example.blog.interceptor;

import com.example.blog.common.context.BaseContext;
import com.example.blog.common.result.Result;
import com.example.blog.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String url = request.getRequestURI().toString();
        if (url.startsWith("/admin/login")){
            log.info("用户正在登录");
            return true;
        }
        if (url.startsWith("/admin/register")){
            log.info("用户正在注册");
            return true;
        }
        String jwt = request.getHeader("Authorization");// 获取请求头中的token
        log.info("用户正在访问:{}",jwt);// 获取请求头中的token
        if (jwt == null){
            log.info("用户未登录");
            response.setStatus(401);
            Result result = Result.failure("用户未登录");
            ObjectMapper mapper = new ObjectMapper();  // Jackson
            String json = mapper.writeValueAsString(result);
            return false;
        }
        try{
            log.info("开始验证token:{}",jwt);
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            Map<String, Object> claims = JwtUtil.parseJwt(jwt);
            Integer adminInfoId = (Integer) claims.get("adminId");
            String key = "adminLogin:" + adminInfoId;
            String jwtInRedis = operations.get(key);
            if (jwtInRedis == null){
                log.info("token已过期");
                throw new RuntimeException("token已过期");
            }
            BaseContext.setCurrentId(adminInfoId);
            return true;
        }catch (Exception e){
            BaseContext.removeCurrentId();
            log.info("token验证失败");
            response.setStatus(401);
            Result result = Result.failure("用户密码或账户出错");
            ObjectMapper mapper = new ObjectMapper();  // Jackson
            String json = mapper.writeValueAsString(result);
            return false;

        }
        //TODO:完善jwt的清除



   }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        BaseContext.removeCurrentId();  // 请求结束后清理 ThreadLocal，防止内存泄漏
    }
    // 请求结束后清理 ThreadLocal，防止内存泄漏
}
