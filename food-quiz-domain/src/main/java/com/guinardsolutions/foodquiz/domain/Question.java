package com.guinardsolutions.foodquiz.domain;

import java.util.List;

public class Question {

    private final String label;
    private final List<String> proposedAnswers;
    private final String correctAnswer;
    private boolean answered;

    public Question(String label, List<String> proposedAnswers, String correctAnswer) {
        this.label = label;
        this.proposedAnswers = proposedAnswers;
        this.correctAnswer = correctAnswer;
        this.answered = false;
    }

    public String getLabel() {
        return label;
    }

    public boolean answer(String answer) {
        this.answered = true;
        return correctAnswer.equals(answer);
    }

    public boolean isAnswered() {
        return answered;
    }

    public List<String> getProposedAnswers() {
        return proposedAnswers;
    }
}
