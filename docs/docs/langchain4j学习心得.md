```markdown
# LangChain4j 学习与配置笔记

## 为什么学习 LangChain4j？
- **LangChain4j** 是目前 Java 生态中最常用、功能最齐全的 AI 应用开发框架，提供了与大模型（LLM）交互的标准化接口和丰富的高级特性。

---

## 一、环境准备与基本配置

### 1. 依赖引入
确保 Spring Boot 版本与 LangChain4j 兼容，在 `pom.xml` 中添加对应依赖（以 OpenAI 为例）：
```xml
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-spring-boot-starter</artifactId>
    <version>最新版本</version>
</dependency>
<!-- 若使用 OpenAI 模型 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
    <version>最新版本</version>
</dependency>
```

### 2. 模型配置
支持两种配置方式：

#### ✅ YML 配置（简单快速）
```yaml
langchain4j:
  open-ai:
    api-key: your-api-key
    model-name: gpt-4
    timeout: 60s
```

#### ✅ 手动配置（更灵活）
通过 Java Config 手动创建 `ChatLanguageModel` 实例，便于动态调整参数，适用于复杂场景。

---

## 二、AI 服务开发方式

### 方式一：自动配置（`@AiService` 注解）
- 编写一个接口，使用 `@AiService` 注解，框架会自动生成代理实现，直接调用即可与 AI 对话。
- **优点**：零配置、上手快，适合快速原型开发。
- **缺点**：扩展复杂功能（如工具、护轨）时需额外处理。

#### 提示词配置
在接口方法上使用 `@SystemMessage` 指定系统提示词，支持从资源文件加载：
```java
@AiService
public interface Assistant {
    @SystemMessage(fromResource = "TiShi.txt")
    String chat(String userMessage);
}
```

### 方式二：手动配置（工厂模式）
- 通过 `AiServices` 工厂类手动构建服务实例，便于注入自定义组件。
- **优点**：灵活可控，便于引入记忆、工具等扩展。
- **缺点**：配置代码量较大。

```java
Assistant assistant = AiServices.builder(Assistant.class)
        .chatLanguageModel(model)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .build();
```

---

## 三、核心功能详解

### 1. 会话记忆（ChatMemory）
- 使用 `MessageWindowChatMemory` 可快速启用记忆功能（保留最近 N 条消息），默认存储在内存中。
- **持久化**：如需长期保存，可配置 Redis 存储（需引入 `langchain4j-redis` 依赖）。

```java
ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
Assistant assistant = AiServices.builder(Assistant.class)
        .chatMemory(chatMemory)
        .build();
```

### 2. RAG（检索增强生成）
- **目的**：让 AI 基于自有知识库（如文档、FAQ）回答，减少幻觉，保证答案准确性。
- **实现步骤**：
  1. 将知识文档（txt、md 等）放入 `resources/docs` 目录。
  2. 配置向量化模型（Embedding Model）将文档片段转换为向量并存储。
  3. 使用时，问题向量与存储向量比对，检索相关片段作为上下文提供给主模型。
- **注意**：RAG 会增加响应延迟，需根据场景权衡使用。

```java
// 典型配置示意（需结合具体实现）
EmbeddingStore embeddingStore = new InMemoryEmbeddingStore();
ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
        .embeddingStore(embeddingStore)
        .embeddingModel(embeddingModel)
        .build();
```

### 3. 工具调用（Tools）
- 允许 AI 调用外部函数，如爬虫、数据库查询、API 调用等。
- 使用 `@Tool` 注解标记方法，框架会自动生成工具描述供模型识别。

```java
public class WeatherTool {
    @Tool("获取指定城市的天气")
    public String getWeather(String city) {
        // 调用第三方 API
        return "晴天";
    }
}
```

### 4. MCP（模型上下文协议）（项目中未使用）
- 需配置 MCP 服务，使 AI 能访问外部数据源（如浏览器搜索、数据库等）。
- 适用于需要实时外部信息的复杂场景（目前以理论学习为主）。

### 5. 护轨（Guardrail）（项目中未使用）
- **输入护轨**：对用户输入进行敏感词过滤、鉴权、格式校验。
- **输出护轨**：对 AI 输出进行内容审核、日志记录。
- 常用于安全与合规需求，保障对话质量。

### 6. SSE 流式接口
- 支持 `TokenStream` 和 Reactor `Flux` 两种流式输出方式。
- **注意**：选择合适的大模型（部分模型流式响应较慢）；若使用 WebFlux，确保 `Flux` 不被序列化为 JSON 字段。
- Spring 原生 `SseEmitter` 使用简单，适合传统 MVC 项目。

```java
// 流式响应示例（TokenStream）
TokenStream stream = assistant.chatStream(userMessage);
stream.onNext(System.out::print)
      .onError(Throwable::printStackTrace)
      .start();
```

---

## 四、总结与建议

- **快速起步**：使用自动配置 + 默认内存记忆，可快速验证功能。
- **生产环境**：考虑手动配置、持久化记忆、RAG 增强和护轨机制，确保稳定与安全。
- **性能优化**：RAG 会降低速度，慎用；流式输出需测试模型兼容性。
- **遇到的一些问题**：使用流式模型，langchain4j中默认会使用异步避免阻塞主线程，但是使用默认的SimpleAsyncTaskExecutor会导致频繁的线程创建和销毁占用资源，所以创建一个专用的线程池更加安全好用。
> 更多详情请参考官方文档：[LangChain4j GitHub](https://github.com/langchain4j/langchain4j)
```
