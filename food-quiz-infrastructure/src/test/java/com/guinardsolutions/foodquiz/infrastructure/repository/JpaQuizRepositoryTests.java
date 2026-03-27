package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.domain.Quiz;
import com.guinardsolutions.foodquiz.infrastructure.entity.ChoiceQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.NumberQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuizEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaQuizRepositoryTests {

    private JpaQuizRepository repo;
    private SpringDataQuizRepository springDataQuizRepository;

    @BeforeEach
    void setup() {
        springDataQuizRepository = mock(SpringDataQuizRepository.class);
        repo = new JpaQuizRepository(springDataQuizRepository);
    }

    @Test
    void should_return_random_quiz() {
        QuestionEntity q1 = new NumberQuestionEntity(1L, "Q1", 140, 10);
        QuestionEntity q2 = new ChoiceQuestionEntity(2L, "Q2", List.of("A", "B", "C"), "B");
        QuizEntity quizEntity = new QuizEntity(1L, UUID.randomUUID().toString(), List.of(q1, q2));
        when(springDataQuizRepository.findRandomQuiz()).thenReturn(Optional.of(quizEntity));

        Optional<Quiz> opt = repo.findRandomQuiz();
        assertThat(opt).isPresent();
        Quiz quiz = opt.get();
        assertThat(quiz.getQuizId().toString()).isEqualTo(quizEntity.getQuizId());
    }
}
