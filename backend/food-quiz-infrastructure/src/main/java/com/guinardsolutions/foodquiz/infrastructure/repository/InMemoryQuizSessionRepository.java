package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryQuizSessionRepository implements QuizSessionRepository {

    private final Map<String, Quiz> sessions = new ConcurrentHashMap<>();

    @Override
    public void save(Quiz quiz) {
        sessions.put(quiz.getQuizId().toString(), quiz);
    }

    @Override
    public Optional<Quiz> findById(String quizId) {
        return Optional.ofNullable(sessions.get(quizId));
    }
}
