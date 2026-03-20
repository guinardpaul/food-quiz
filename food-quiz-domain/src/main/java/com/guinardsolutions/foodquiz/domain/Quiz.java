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
        return questions.get(index);
    }

    public boolean answer(String answer) {
        return questions.get(index).answer(answer);
    }
}
