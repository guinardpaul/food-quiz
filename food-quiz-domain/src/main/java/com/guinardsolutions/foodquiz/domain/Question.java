package com.guinardsolutions.foodquiz.domain;

import java.util.List;

public class Question {

    private final String label;
    private final List<String> possibleAnswers;
    private final String correctAnswer;

    public Question(String label, List<String> possibleAnswer, String correctAnswer) {
        this.label = label;
        this.possibleAnswers = possibleAnswer;
        this.correctAnswer = correctAnswer;
    }

    public String getLabel() {
        return label;
    }

    public boolean answer(String answer) {
        return correctAnswer.equals(answer);
    }
}
