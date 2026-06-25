package com.example.blog.langchainmodle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper mapper;

    private static final String REDIS_KEY_PREFIX = "chat:memory:";

    private String getkey(Object memoryId) {
        return REDIS_KEY_PREFIX + memoryId.toString();
    }


    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
       try {
           String key = getkey(memoryId);
           String json = stringRedisTemplate.opsForValue().get(key);// 从redis中获取数据

           //trim().isEmpty()表示去除空格后的字段，用于避免用户输入空格，防止缓存穿透
           if (json == null || json.trim().isEmpty()) {
//              log.info("redis中没有找到对应的会话:{}", memoryId);
              return new ArrayList<>();
           }
           //补充从数据库中获取数据，存储到redis中
           List<ChatMessage> messages = ChatMessageDeserializer.messagesFromJson(json);
           return messages;

       }catch (Exception e){
//           log.error("从redis中获取数据失败", e);
           return new ArrayList<>();
       }
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        try {

            String key = getkey(memoryId);
            String json = ChatMessageSerializer.messagesToJson(messages);

            // 设置1天过期时间，避免内存泄漏
            stringRedisTemplate.opsForValue().set(key, json, Duration.ofDays(1));

//            log.debug("更新会话内容: {}", messages.size(), memoryId);
        } catch (Exception e) {
//            log.error("更新失败: {}", memoryId, e);
        }


    }

    @Override
    public void deleteMessages(Object memoryId) {

        try {
            String key = getkey(memoryId);
            Boolean deleted = stringRedisTemplate.delete(key);

            if (Boolean.TRUE.equals(deleted)) {
//                log.debug("删除会话缓存: {}", memoryId);
            } else {
//                log.debug("缓存不存在: {}", memoryId);
            }
        } catch (Exception e) {
//            log.error("缓存删除失败: {}", memoryId, e);
        }

    }
}
