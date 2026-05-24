package com.guinardsolutions.foodquiz.application.port.in;

import java.util.List;

public record QuestionResponse(
        String label,
        String foodName,
        String imageUrl,
        String portionDescription,
        List<String> proposedAnswers
) {}
