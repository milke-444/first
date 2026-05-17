package com.example.blog.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SplitSentenceRequest {

    @NotBlank(message = "段落不能为空")
    private String paragraph;

}
