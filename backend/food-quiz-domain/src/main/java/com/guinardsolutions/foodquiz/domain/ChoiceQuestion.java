package com.guinardsolutions.foodquiz.domain;

import java.util.List;

public class ChoiceQuestion extends Question {

    private final List<String> proposedAnswers;
    private final String correctAnswer;

    public ChoiceQuestion(String label, List<String> proposedAnswers, String correctAnswer) {
        super(label);
        this.proposedAnswers = proposedAnswers;
        this.correctAnswer = correctAnswer;
    }

    @Override
    boolean answer(String answer) {
        this.answered = true;
        return this.correctAnswer.equals(answer);
    }

    public List<String> getProposedAnswers() {
        return proposedAnswers;
    }
}
