package com.guinardsolutions.foodquiz.api.dto;

import java.util.Map;

public record FeedbackDto(
        boolean correct,
        String correctAnswer,
        Map<String, Integer> equivalents,
        String glycemicImpact,
        boolean hasNextQuestion
) {}
