package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.application.port.out.QuizRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import com.guinardsolutions.foodquiz.infrastructure.mapper.QuizMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaQuizRepository implements QuizRepository {

    private final SpringDataQuizRepository springDataQuizRepository;
    private final QuizMapper quizMapper;

    public JpaQuizRepository(SpringDataQuizRepository springDataQuizRepository) {
        this.springDataQuizRepository = springDataQuizRepository;
        this.quizMapper = new QuizMapper();
    }

    @Override
    public Optional<Quiz> findRandomQuiz() {
        return springDataQuizRepository.findRandomQuiz().map(quizMapper::toDomain);
    }

}
