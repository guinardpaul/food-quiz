package com.guinardsolutions.foodquiz.domain;

import java.util.List;
import java.util.UUID;

public class Quiz {

    private final QuizId quizId;
    private final List<Question> questions;
    private final QuizReview quizReview;
    private int index = -1;

    public Quiz(String uuid, List<Question> questions) {
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("A Quiz must have at least one question");
        }
        this.quizId = new QuizId(UUID.fromString(uuid));
        this.questions = questions;
        this.quizReview = new QuizReview(questions.size());
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
        Question question = questions.get(index);
        boolean result = question.answer(answer);
        quizReview.addUserAnswer(question, answer, result);

        return result;
    }

    public boolean hasQuestionLeft() {
        return (index + 1) < questions.size();
    }

    public double getScore() {
        if (hasQuestionLeft() || !questions.get(index).isAnswered()) {
            throw new IllegalStateException("All quiz questions must be answered before getting the score");
        }

        return this.quizReview.computeScore();
    }

    public QuizReview getQuizReview() {
        return quizReview;
    }

    public UUID getQuizId() {
        return quizId.value();
    }
}
