
# AI 辅助个人学习工作台

我的第一个全栈项目，集成高并发点赞、热度排行榜、AI 英语分析、简历生成等功能，持续迭代中。

## 技术栈

| 层级 | 技术 |
| :--- | :--- |
| 后端 | Spring Boot 3、MyBatis、MySQL、Redis、Lua |
| 前端 | Vue 3、Element Plus |
| AI | DeepSeek（Spring AI 集成） |
| 安全 |JWT|
| 工具 |Git、Apifox |

## 快速开始

```bash
# 1. 克隆项目
git clone https://github.com/milke-444/first.git
cd first

# 2. 配置环境变量（复制 .env.example 为 .env，填入你的密钥）
cp .env.example .env
```

访问 `http://localhost:8080/doc.html` 查看 API 文档。(未完善实现)

## 核心功能

### 1. 高并发点赞（Redis + Lua 原子操作）

**问题**：多线程并发下，`SISMEMBER` 判断与 `SADD`/`SREM` 操作之间产生竞态窗口，导致点赞数与实际不符。

**方案**：将"判断 + Set 操作 + ZSet 操作"封装为 Lua 脚本，利用 Redis 单线程保证原子性。

**效果**：单用户 100 线程并发测试数据一致，Redis 请求从 3 次降为 1 次。

> 详见 [点赞功能并发优化记录](docs/docs/解决点赞并发以及分析.md)

### 2. 热度排行榜（多级缓存 + N+1 优化）

**问题**：排行榜循环逐条查数据库获取博客名称，产生 N+1 查询。

**方案**：
- ZSet 存点赞时间戳，实现热度排序
- Hash 存博客名称，`HMGET` 批量获取
- 缓存未命中时批量查 MySQL 并回填 Redis

**效果**：数据库查询从 N 次降为 0-1 次，缓存命中率可观测。

### 3. 内容分类体系

支持两种内容类型：博客文章（BLOG）、学习笔记（NOTE）、简历生成，可按类型筛选和标签检索。

### 4. AI 英语句子分析（待完善）

集成 DeepSeek，输入英语长难句，自动分析主谓宾定状补，辅助四级备考。

### 5. 简历生成（待优化）

用户可以自由组织简历模块（教育背景、项目经历、荣誉奖项等），条目内容通过 JSON 动态存储，支持模板切换和 PDF 导出。

## 工程规范

| 规范 | 说明 |
| :--- | :--- |
| 统一返回格式 | `Result<T>` 统一包装所有接口返回值 |
| 全局异常处理 | `@RestControllerAdvice` + 自定义 `UserException` |
| 状态码枚举 | `ResultCodeEnum` 集中管理业务状态码 |
| 参数校验 | `@Valid` + `@NotBlank`/`@NotNull` 自动校验 |
| 接口限流 | 基于 Redis + Lua 的 `@RateLimit` 注解 |
| 日志脱敏 | AOP 异步记录操作日志，密码自动脱敏 |
| ThreadLocal 清理 | 拦截器 `afterCompletion` 中调用 `remove()` 防止内存泄漏 |
| 敏感信息隔离 | `.env` 环境变量，`.gitignore` 防止密钥泄露 |

## API 文档

项目已集成 Knife4j，启动后访问：

```
http://localhost:8080/doc.html
```

## 学习笔记

项目开发过程中的技术沉淀，存放在 `docs/` 目录：

- [B+Tree 索引原理与最左前缀原则](docs/docs/B+Tree.md)
- [Redis 底层数据结构：SDS、ziplist、skiplist](docs/docs/redis-string.md)
- [Lua 脚本简单理解](docs/docs/lua脚本.md)
- [Spring Boot 自动装配原理](docs/docs/spring-boot-auto-configuration.md)(未完善)
- [MySQL JSON 字段使用实践](docs/docs/mysql-json-field.md)(未完善)
- [langchain4j的基本学习和一些心得](docs/docs/langchain4j学习心得.md)(学到新东西后续会加深)
- [redis持久化的理论学习](docs/docs/reids持久化学习.md)

## 项目优化历程

| 阶段 | 内容 | 状态 |
| :--- | :--- | :--- |
| 阶段一 | 结构工程化（包名规范、统一返回、异常处理、ThreadLocal 清理） | ✅ 完成 |
| 阶段二 | 内容分类体系搭建（文章类型标签、分类筛选） | ✅ 完成 |
| 阶段三 | 简历生成和pdf导出 | ✅ 初步完成,优化简历样式,嵌入ai辅助完善 |
| 阶段四 | AI 功能集成（智能助手） |✅基于langchain4j和质谱大模型实现，后续完善一些技术的使用 |
| 阶段五 | 个人工作台知识笔记功能 | 🚧 进行中 |
| 阶段六 | redis持久化 | ✅初步完成 |
| 阶段七 | Docker 容器化部署 | 📅 计划中 |
| 阶段八 | 单元测试与压力测试 | 📅 计划中 | 
| 阶段九 | 对数据库表进行整体优化 | 🚧 进行中 |
| 阶段十 | 完善消息队列和权限控制等功 | 🚧 进行中 |
| 阶段十一 | 优化为springclound | 🚧 进行中 |



