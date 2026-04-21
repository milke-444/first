package com.example.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling//开启定时任务
@EnableAsync  // 开启异步
public class FirstBiogApplication {

    public static void main(String[] args) {
        SpringApplication.run(FirstBiogApplication.class, args);
    }

}
