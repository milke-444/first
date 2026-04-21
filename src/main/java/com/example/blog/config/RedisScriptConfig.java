package com.example.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration// @Configuration 标注类，表示该类是 Spring 配置类，用于定义 Spring 容器中的 Bean。
public class RedisScriptConfig {
    @Bean// @Bean 标注方法，表示该方法返回的 Bean 将被注册到 Spring 容器中。
    public RedisScript<Long> toggleLikeScript() {
        // 使用 of() 方法简化构建
        return RedisScript.of(new ClassPathResource("toggleLike.lua"), Long.class);//指定要加载的lua脚本位置，和其返回值类型，并和并为一个redisScript对象返回
    }

}
