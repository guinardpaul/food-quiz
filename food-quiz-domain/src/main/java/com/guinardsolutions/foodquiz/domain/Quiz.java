package com.guinardsolutions.foodquiz.domain;

import java.util.List;

public class Quiz {

    List<Question> questions;
    int index = -1;

    public Quiz(List<Question> questions) {
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("A Quiz must have at least one question");
        }
        this.questions = questions;
    }

    public Question currentQuestion() {
        index++;
        if (index >= questions.size()) {
            throw new IllegalStateException("There is no question left");
        }
        if (index > 0) {
            Question previousQuestion = questions.get(index - 1);
            if (!previousQuestion.isAnswered()) {
                throw new IllegalStateException("Cannot get next question because current has not been answered");
            }
        }

        return questions.get(index);
    }

    public boolean answer(String answer) {
        return questions.get(index).answer(answer);
    }

    public boolean hasQuestionLeft() {
        return (index + 1) < questions.size();
    }
}
