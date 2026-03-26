package com.guinardsolutions.foodquiz.infrastructure.mapper;

import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.NumberQuestion;
import com.guinardsolutions.foodquiz.domain.Question;
import com.guinardsolutions.foodquiz.domain.Quiz;
import com.guinardsolutions.foodquiz.infrastructure.entity.ChoiceQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.NumberQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuizEntity;

public class QuizMapper {

    public Quiz toDomain(QuizEntity quizEntity) {
        return new Quiz(
                quizEntity.getQuizId(),
                quizEntity.getQuestions().stream().map(this::toDomain).toList()
        );
    }

    public Question toDomain(QuestionEntity questionEntity) {
        if (questionEntity instanceof ChoiceQuestionEntity choiceQuestionEntity) {
            return new ChoiceQuestion(
                    choiceQuestionEntity.getLabel(),
                    choiceQuestionEntity.getProposedAnswer(),
                    choiceQuestionEntity.getCorrectAnswer()
            );
        } else if (questionEntity instanceof NumberQuestionEntity numberQuestionEntity) {
            return new NumberQuestion(
                    numberQuestionEntity.getLabel(),
                    numberQuestionEntity.getExpectedValue(),
                    numberQuestionEntity.getTolerance()
            );
        } else {
            throw new IllegalArgumentException("Not implemented yet");
        }
    }
}
