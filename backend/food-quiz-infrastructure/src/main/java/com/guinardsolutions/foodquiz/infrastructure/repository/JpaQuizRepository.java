package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.application.port.out.QuizRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.mapper.QuizMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaQuizRepository implements QuizRepository {

    private static final int MAX_QUESTIONS = 20;

    private final SpringDataQuestionRepository springDataQuestionRepository;
    private final QuizMapper quizMapper;

    public JpaQuizRepository(SpringDataQuestionRepository springDataQuestionRepository) {
        this.springDataQuestionRepository = springDataQuestionRepository;
        this.quizMapper = new QuizMapper();
    }

    @Override
    public Optional<Quiz> findRandomQuiz(int count) {
        int safeCount = Math.min(count, MAX_QUESTIONS);
        List<QuestionEntity> entities = springDataQuestionRepository.findRandomQuestions(safeCount);
        if (entities.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(quizMapper.toDomain(entities));
    }

}
