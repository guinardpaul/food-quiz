package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.domain.NumberQuestion;
import com.guinardsolutions.foodquiz.domain.Quiz;
import com.guinardsolutions.foodquiz.infrastructure.entity.ChoiceQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.NumberQuestionEntity;
import com.guinardsolutions.foodquiz.infrastructure.entity.QuestionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JpaQuizRepositoryTests {

    private JpaQuizRepository repo;
    private SpringDataQuestionRepository springDataQuestionRepository;

    @BeforeEach
    void setup() {
        springDataQuestionRepository = mock(SpringDataQuestionRepository.class);
        repo = new JpaQuizRepository(springDataQuestionRepository);
    }

    @Test
    void should_return_quiz_assembled_from_random_questions() {
        QuestionEntity q1 = new NumberQuestionEntity(1L, "Q1", 140, 10);
        QuestionEntity q2 = new ChoiceQuestionEntity(2L, "Q2", List.of("A", "B", "C"), "B");
        when(springDataQuestionRepository.findRandomQuestions(2)).thenReturn(List.of(q1, q2));

        Optional<Quiz> opt = repo.findRandomQuiz(2);

        assertThat(opt).isPresent();
        Quiz quiz = opt.get();
        assertThat(quiz.getQuizId()).isNotNull();
        assertThat(quiz.currentQuestion()).isInstanceOf(NumberQuestion.class);
    }

    @Test
    void should_return_empty_when_no_questions_in_db() {
        when(springDataQuestionRepository.findRandomQuestions(5)).thenReturn(List.of());

        Optional<Quiz> opt = repo.findRandomQuiz(5);

        assertThat(opt).isEmpty();
    }
}
