package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;

public interface GetCurrentQuestionUseCase {

    QuestionResponse getCurrentQuestion(String quizId);
}
