package com.example.blog.service;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Flux;


@AiService(contentRetriever = "contentRetriever")
public interface AIService {

    @SystemMessage(fromResource = "TiShi.txt")
//    String chat(@MemoryId String sessionId, @UserMessage String message);//启用会话记忆
    Flux<String> chatStream(@MemoryId String sessionId, @UserMessage String userMessage);
}
