package com.guinardsolutions.foodquiz.api.mapper;

import com.guinardsolutions.foodquiz.api.dto.QuestionDto;
import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;

public class QuestionMapper {

    public QuestionDto toDto(QuestionResponse response) {
        return new QuestionDto(
                response.label(),
                response.foodName(),
                response.imageUrl(),
                response.portionDescription(),
                response.proposedAnswers()
        );
    }
}
