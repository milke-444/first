package com.example.blog.controller.admin;

import com.example.blog.common.result.Result;
import com.example.blog.model.dto.AnalyzeSentenceRequest;
import com.example.blog.model.dto.SplitSentenceRequest;
import com.example.blog.service.AIService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 分析英文句子成分
     *
     * 流程：
     *   前端 POST {"sentence": "I love programming"} 
     *   → Controller 接收参数 → 调用 Service 层
     *   → Service 调用 DeepSeek API → 返回分析结果
     *   → Controller 用 Result.success() 包装后返回给前端
     *
     * @Valid: 触发 Jakarta Validation 框架自动校验请求参数
     *         AnalyzeSentenceRequest 中的 @NotBlank 注解会检查 sentence 是否为空
     *         如果校验失败，由 GlobalExceptionHandler 统一处理并返回错误信息
     * @RequestBody: 将 HTTP 请求体中的 JSON 自动反序列化为 Java 对象
     *               前端传来的 {"sentence": "..."} 会自动映射到 DTO 的 sentence 字段
     */
    @PostMapping("/analyze-sentence")
    public Result analyzeSentence(@Valid @RequestBody AnalyzeSentenceRequest request) {
        log.info("收到句子分析请求: {}", request.getSentence());

        // 调用 Service 层执行业务逻辑
        // Controller 层只做"数据接收"和"结果返回"两个事
        // 实际的 AI 调用逻辑封装在 AIServiceImpl 中
        String result = aiService.analyzeSentence(request.getSentence());

        // 用统一的 Result.success() 包装返回
        // 这样前端收到的格式始终是 {"code": 1, "message": "操作成功！", "data": ...}
        // 前端不需要为不同的接口写不同的解析逻辑
        return Result.success(result);
    }

    /**
     * 拆分长难句并翻译
     *
     * 流程：
     *   前端 POST {"paragraph": "..."}
     *   → Controller 接收 → Service 调 DeepSeek → 返回结果
     *
     * 为什么要单独写一个接口而不是和 analyze-sentence 合并？
     *   虽然底层都是调 DeepSeek，但：
     *     1. 两个功能的 Prompt 完全不同（分析成分 vs 拆分翻译）
     *     2. 返回的数据结构不同（句子成分 JSON vs 句子列表 JSON）
     *     3. 未来可能独立演进（比如拆分翻译要加流式输出、缓存等）
     *   接口分开设计更符合"单一职责原则"：一个接口只做一件事
     */
    @PostMapping("/split-sentence")
    public Result splitSentence(@Valid @RequestBody SplitSentenceRequest request) {
        log.info("收到长难句拆分请求，段落长度: {}", request.getParagraph().length());

        String result = aiService.splitSentence(request.getParagraph());

        return Result.success(result);
    }

}
