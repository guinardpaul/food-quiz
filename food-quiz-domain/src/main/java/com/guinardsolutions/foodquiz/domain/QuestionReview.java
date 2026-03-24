package com.guinardsolutions.foodquiz.domain;

public class QuestionReview {

    private final String question;
    private final String userAnswer;
    private final String correctAnswer;
    private final boolean isCorrect;

    public QuestionReview(Question question, String userAnswer) {
        this.question = question.getLabel();
        this.userAnswer = userAnswer;
        this.correctAnswer = question.getCorrectAnswer();
        this.isCorrect = userAnswer.equals(question.getCorrectAnswer());
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
