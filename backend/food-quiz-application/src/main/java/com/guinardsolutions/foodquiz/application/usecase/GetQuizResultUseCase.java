package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;

public interface GetQuizResultUseCase {

    QuizResultResponse getResult(String quizId);
}
