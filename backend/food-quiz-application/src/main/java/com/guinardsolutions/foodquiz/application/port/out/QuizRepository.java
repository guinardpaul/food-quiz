package com.guinardsolutions.foodquiz.application.port.out;

import com.guinardsolutions.foodquiz.domain.Quiz;

import java.util.Optional;

public interface QuizRepository {

    Optional<Quiz> findRandomQuiz(int count);
}
