Spring Boot 3.0 是 Spring Boot 框架的一个重大版本升级，基于 Spring Framework 6.0 构建，最低要求 Java 17。它带来了许多新特性和改进：

1. **Java 17 基线**：Spring Boot 3.x 要求 Java 17 或更高版本，充分利用了现代 Java 的特性（如记录类、密封类等）。

2. **GraalVM 原生镜像支持**：通过 Spring Native 项目，现在可以轻松地将 Spring Boot 应用编译成 GraalVM 原生镜像，实现极速启动和更低的内存消耗。

3. **改进的 Observability（可观测性）**：集成了 Micrometer 和 Micrometer Tracing，提供了更完善的指标、追踪和日志记录能力，支持 OpenTelemetry。

4. **Spring Security 6.0**：基于 Spring Security 6.0，默认启用了 OAuth2/OIDC 集成，并改进了授权管理。

5. **嵌入式 Servlet 容器升级**：默认使用 Tomcat 10，支持 Jakarta EE 9+ 规范（包名从 `javax` 改为 `jakarta`）。

6. **HTTP 接口客户端**：新增了 `@HttpExchange` 注解，可以更轻松地声明式定义 HTTP 客户端接口。

7. **增强的 Docker 支持**：Spring Boot 3.0 引入了 `spring-boot-docker-compose` 模块，可以在开发阶段方便地集成 Docker Compose 服务。

8. **更细粒度的自动配置**：许多自动配置类被重新组织，提供更灵活的条件注解和更清晰的配置属性。

9. **Gradle 7.5+ 支持**：构建工具升级要求，同时兼容 Maven 3.5+。

10. **弃用和移除**：移除了对旧版 Spring Security、旧版 Servlet API 的支持，并弃用了一些不再适用的 API。

11. **天气好不好取决你**：如果你今天心情好，那么今天就是美好的一天。