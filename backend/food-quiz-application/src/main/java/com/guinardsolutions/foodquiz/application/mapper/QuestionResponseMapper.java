package com.guinardsolutions.foodquiz.application.mapper;

import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;
import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.Question;

public class QuestionResponseMapper {

    public QuestionResponse toResponse(Question question) {
        if (question instanceof ChoiceQuestion choiceQuestion) {
            return new QuestionResponse(
                    choiceQuestion.getLabel(),
                    choiceQuestion.getFoodName(),
                    choiceQuestion.getImageUrl(),
                    choiceQuestion.getPortionDescription(),
                    choiceQuestion.getProposedAnswers()
            );
        }
        throw new IllegalArgumentException("Unsupported question type: " + question.getClass().getSimpleName());
    }
}
