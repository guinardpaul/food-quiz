package com.guinardsolutions.foodquiz.api.dto;

import java.util.List;

public record QuestionDto(
        String label,
        String foodName,
        String imageUrl,
        String portionDescription,
        List<String> proposedAnswers
) {}
