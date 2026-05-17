package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnalyzeSentenceRequest {

    @NotBlank(message = "句子不能为空")
    private String sentence;

}
