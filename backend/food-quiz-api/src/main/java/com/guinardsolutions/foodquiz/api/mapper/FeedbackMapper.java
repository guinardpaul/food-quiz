package com.guinardsolutions.foodquiz.api.mapper;

import com.guinardsolutions.foodquiz.api.dto.FeedbackDto;
import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;

public class FeedbackMapper {

    public FeedbackDto toDto(FeedbackResponse response) {
        return new FeedbackDto(
                response.correct(),
                response.correctAnswer(),
                response.equivalents(),
                response.glycemicImpact(),
                response.hasNextQuestion()
        );
    }
}
