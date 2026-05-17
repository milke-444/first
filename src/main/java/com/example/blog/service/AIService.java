package com.example.blog.service;

public interface AIService {

    /**
     * 分析英文句子成分
     */
    String analyzeSentence(String sentence);

    /**
     * 拆分长难句并翻译
     */
    String splitSentence(String paragraph);

}
