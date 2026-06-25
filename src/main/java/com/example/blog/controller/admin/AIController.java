package com.example.blog.controller.admin;

import com.example.blog.service.AIService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Autowired
    private AIService aiService;

    /**
     * 流式 AI 对话接口。
     * 直接用 HttpServletResponse 手动 flush，确保每个 token 都立即推送到前端，
     * 不受任何中间层缓冲影响。
     * 没有使用webflux导致流式结果变成json串，使用了原生的pring MVC 原生支持的 SseEmitter。
     */

    @PostMapping("/analyze-sentence")
    public void analyzeSentence(@RequestBody String message,
                                                       HttpSession session,
                                                       HttpServletResponse response) throws IOException {
        // 设置 SSE 响应头，禁用所有缓冲
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
        response.flushBuffer();

        var writer = response.getWriter();

        aiService.chatStream(session.getId(), message)
                .filter(chunk -> chunk != null && !chunk.isBlank())
                .subscribe(
                        chunk -> {
                            try {
                                writer.write("data:" + chunk + "\n\n");
                                writer.flush();
                            } catch (Exception e) {
                                // 客户端已断开
                            }
                        },
                        error -> log.error("AI 流式输出异常", error),
                        () -> writer.flush()
                );
    }

}
