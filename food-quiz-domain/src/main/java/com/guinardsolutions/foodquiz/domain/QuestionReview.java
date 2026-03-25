package com.guinardsolutions.foodquiz.domain;

public class QuestionReview {

    private final String question;
    private final String userAnswer;
    private final boolean isCorrect;

    public QuestionReview(Question question, String userAnswer, boolean isCorrect) {
        this.question = question.getLabel();
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
