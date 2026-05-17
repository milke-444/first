package com.example.blog.service.impl;

import com.example.blog.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AIServiceImpl implements AIService {

    // 注入 Spring AI 自动配置好的 ChatModel 对象
    // spring-ai-starter-model-deepseek 会根据 application.yml 中 spring.ai.deepseek.* 的配置
    // 自动创建一个连接到 DeepSeek API 的 ChatModel Bean，我们直接注入使用即可
    @Autowired
    private ChatModel chatModel;

    /**
     * 句子分析功能的 Prompt 模板（常量）
     * systemPrompt: 给 AI 设定角色身份，告诉它"你是谁、该怎么回答"
     *              这里的 role 是 "system"（系统消息），设定 AI 的身份为英语语法专家
     * userPrompt:  用户的输入内容。这里我们把用户的句子嵌入到固定的分析模板中
     *              用 %s 占位符在运行时替换为真实的句子
     * 所有数据通过双引号包裹，方便后续用 JSON 格式统一解析
     */
    private static final String ANALYZE_SYSTEM_PROMPT = """
            你是一位专业的英语语法分析专家。
            你的任务是分析用户输入的英文句子，返回 JSON 格式的分析结果。
            只返回 JSON，不要包含其他说明文字。
            """;

    private static final String ANALYZE_USER_PROMPT_TEMPLATE = """
            请分析以下英文句子：

            句子：%s

            请严格按照以下 JSON 格式返回（不要带 markdown 代码块标记）：
            {
              "subject": "主语",
              "predicate": "谓语动词",
              "object": "宾语（若无填 null）",
              "tense": "时态",
              "sentenceType": "句子类型（简单句/并列句/复合句）",
              "structure": "句子结构分析说明",
              "chineseTranslation": "中文翻译"
            }
            """;

    /**
     * 长难句拆分功能的 Prompt 模板（常量）
     */
    private static final String SPLIT_SYSTEM_PROMPT = """
            你是一位专业的英语学习助手，擅长分析长难句。
            你的任务是将复杂的英文段落拆分为单个句子，并提供翻译和关键词汇说明。
            只返回 JSON，不要包含其他说明文字。
            """;

    private static final String SPLIT_USER_PROMPT_TEMPLATE = """
            请分析以下英文段落：

            段落：%s

            请严格按照以下 JSON 格式返回（不要带 markdown 代码块标记）：
            {
              "totalSentences": 总句子数,
              "sentences": [
                {
                  "english": "原文句子",
                  "chinese": "中文翻译",
                  "keywords": ["关键词1", "关键词2"]
                }
              ]
            }
            """;

    @Override
    public String analyzeSentence(String sentence) {
        // 1. 构建发送给 DeepSeek 的消息内容
        //    把用户输入的句子嵌入到预设的分析模板中
        String userPrompt = String.format(ANALYZE_USER_PROMPT_TEMPLATE, sentence);

        // 2. 调用 DeepSeek API（通过 Spring AI 的 ChatModel）
        //    chatModel.call(prompt) 等价于向 DeepSeek 发送一条消息并获取回复
        //    Spring AI 底层自动处理了：
        //      - HTTP 请求的发送（POST 到 https://api.deepseek.com/chat/completions）
        //      - API Key 的鉴权（使用 application.yml 中配置的 api-key）
        //      - 请求/响应的序列化与反序列化
        //    我们无需手动拼 HTTP 请求，一行代码即可完成调用
        String fullPrompt = ANALYZE_SYSTEM_PROMPT + "\n\n" + userPrompt;// 完整的 Prompt
        String response = chatModel.call(fullPrompt);

        // 3. 记录调用日志
        log.info("DeepSeek analyzeSentence 调用完成，输入句子长度: {}", sentence.length());

        // 4. 直接返回 AI 的回复内容
        //    由于我们在 Prompt 中要求 AI 返回 JSON，response 就是一个 JSON 字符串
        return response;
    }

    @Override
    public String splitSentence(String paragraph) {
        // 1. 构建提示词
        String userPrompt = String.format(SPLIT_USER_PROMPT_TEMPLATE, paragraph);

        // 2. 调用 DeepSeek API
        //    chatModel.call() 是最简单的调用方式：
        //    传入一个完整的 prompt 字符串（system + user），返回 AI 生成的文本
        //    注意：Spring AI 还提供了更高级的用法：
        //      - ChatClient：流式调用、函数调用等
        //      - PromptTemplate + Message：结构化消息构建
        //      - OutputParser：自动将 AI 回复解析为 Java 对象
        //    本项目先用最基本的 call() 方法，熟悉后再升级
        String fullPrompt = SPLIT_SYSTEM_PROMPT + "\n\n" + userPrompt;
        String response = chatModel.call(fullPrompt);

        // 3. 记录日志
        log.info("DeepSeek splitSentence 调用完成，输入段落长度: {}", paragraph.length());

        // 4. 返回处理结果
        return response;
    }

}
