package com.guinardsolutions.foodquiz.application.mapper;

import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.domain.Quiz;

public class QuizResponseMapper {

    public QuizResponse toResponse(Quiz quiz) {
        return new QuizResponse(
                quiz.getQuizId().toString()
        );
    }
}
