package com.guinardsolutions.foodquiz.application.mapper;

import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;
import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.GlycemicImpact;

public class FeedbackResponseMapper {

    public FeedbackResponse toResponse(boolean correct, ChoiceQuestion question, boolean hasNextQuestion) {
        GlycemicImpact impact = question.getGlycemicImpact();
        return new FeedbackResponse(
                correct,
                question.getCorrectAnswer(),
                question.getEquivalents(),
                impact != null ? impact.name() : null,
                hasNextQuestion
        );
    }
}
