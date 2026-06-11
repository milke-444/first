package com.example.blog.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j / Swagger 接口文档配置。
 *
 * 访问地址：http://localhost:8080/doc.html
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("简历生成系统 API 文档")
                        .version("1.0")
                        .description("简历生成系统的后端接口文档，支持简历的增删改查、PDF导出等功能")
                        .contact(new Contact()
                                .name("开发者")
                                .email("dev@example.com")));
    }
}
