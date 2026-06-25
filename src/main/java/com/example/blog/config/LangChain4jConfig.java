package com.example.blog.config;

import com.example.blog.config.rag.RagConfig;
import com.example.blog.langchainmodle.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LangChain4jConfig {
    @Bean
    public ChatMemoryStore chatMemoryStore() {
        return new RedisChatMemoryStore();
    }

    @Bean
    public ChatMemoryProvider chatMemoryProvider(ChatMemoryStore chatMemoryStore) {
        // 正确写法：返回一个 lambda，为每个 memoryId 创建一个滑动窗口记忆
        // 重写了redischatmemorystore的实现，在框架自动调用getMessages方法时，会调用redischatmemorystore的getMessages方法
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)                 // 绑定会话 ID
                .maxMessages(10)              // 滑动窗口大小：只保留最近 10 条消息
                .chatMemoryStore(chatMemoryStore) // 绑定 Redis 存储
                .build();
    }
}
