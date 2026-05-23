package com.guinardsolutions.foodquiz.application.port.in;

import java.util.Map;

public record FeedbackResponse(
        boolean correct,
        String correctAnswer,
        Map<String, Integer> equivalents,
        String glycemicImpact,
        boolean hasNextQuestion
) {}
