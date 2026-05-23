package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;

public interface AnswerQuestionUseCase {

    FeedbackResponse answerQuestion(String quizId, String answer);
}
