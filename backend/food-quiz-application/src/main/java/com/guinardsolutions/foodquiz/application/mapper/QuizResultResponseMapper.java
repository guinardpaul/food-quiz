package com.guinardsolutions.foodquiz.application.mapper;

import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;
import com.guinardsolutions.foodquiz.domain.Quiz;

public class QuizResultResponseMapper {

    public QuizResultResponse toResponse(Quiz quiz) {
        int score = (int) quiz.getScore();
        int total = quiz.getQuizReview().getQuestionReview().size();
        return new QuizResultResponse(score, total);
    }
}
