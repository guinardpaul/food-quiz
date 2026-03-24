package com.guinardsolutions.foodquiz.domain;

import java.util.List;

public class Question {

    private final String label;
    private final List<String> proposedAnswers;
    protected final String correctAnswer;
    protected boolean answered;

    public Question(String label, List<String> proposedAnswers, String correctAnswer) {
        this.label = label;
        this.proposedAnswers = proposedAnswers;
        this.correctAnswer = correctAnswer;
        this.answered = false;
    }

    public String getLabel() {
        return label;
    }

    boolean answer(String answer) {
        this.answered = true;
        return this.correctAnswer.equals(answer);
    }

    public boolean isAnswered() {
        return answered;
    }

    public List<String> getProposedAnswers() {
        return proposedAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
