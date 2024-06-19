package com.example.ITHubot.Dto;

import lombok.Getter;

import java.util.Map;
@Getter
public class ResultRequest {
    private Long userId;
    private Long testId;
    private Map<Long, Long> userAnswers; // ключ - идентификатор вопроса, значение - идентификатор ответа


}
