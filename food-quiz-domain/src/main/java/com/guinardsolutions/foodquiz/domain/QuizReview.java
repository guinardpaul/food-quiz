package com.guinardsolutions.foodquiz.domain;


import java.util.ArrayList;
import java.util.List;

public class QuizReview {

    private final List<QuestionReview> questionReview;
    private final int total;
    private double score;

    public QuizReview(int total) {
        this.total = total;
        this.score = 0;
        this.questionReview = new ArrayList<>();
    }

    public double computeScore() {
        int correctAnswers = questionReview.stream().filter(QuestionReview::isCorrect).toList().size();
        score = Math.floor(((double) correctAnswers / total) * 100);
        return score;
    }

    public void addUserAnswer(Question question, String userAnswer) {
        questionReview.add(new QuestionReview(question, userAnswer));
    }

    public List<QuestionReview> getQuestionReview() {
        return questionReview;
    }
}
