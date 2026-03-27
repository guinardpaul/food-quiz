package com.guinardsolutions.foodquiz.api.mapper;

import com.guinardsolutions.foodquiz.api.dto.QuizDto;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;

public class QuizMapper {

    public QuizDto toDto(QuizResponse quizResponse) {
        return new QuizDto(
                quizResponse.quizId()
        );
    }
}
