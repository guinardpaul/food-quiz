package com.guinardsolutions.foodquiz.application.port.out;

import com.guinardsolutions.foodquiz.domain.Quiz;

import java.util.Optional;

public interface QuizSessionRepository {

    void save(Quiz quiz);

    Optional<Quiz> findById(String quizId);
}
