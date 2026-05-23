package com.guinardsolutions.foodquiz.application;

import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.application.usecase.GetQuizResultService;
import com.guinardsolutions.foodquiz.application.usecase.GetQuizResultUseCase;
import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetQuizResultServiceTests {

    private GetQuizResultUseCase service;
    private QuizSessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        sessionRepository = mock(QuizSessionRepository.class);
        service = new GetQuizResultService(sessionRepository);
    }

    @Test
    void should_throw_when_session_not_found() {
        when(sessionRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getResult("unknown"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_return_score_after_all_questions_answered() {
        String quizId = UUID.randomUUID().toString();
        ChoiceQuestion q1 = new ChoiceQuestion("Q1", List.of("10g", "20g"), "20g");
        ChoiceQuestion q2 = new ChoiceQuestion("Q2", List.of("30g", "50g"), "50g");
        Quiz quiz = new Quiz(quizId, List.of(q1, q2));
        quiz.currentQuestion();
        quiz.answer("20g");
        quiz.currentQuestion();
        quiz.answer("30g");
        when(sessionRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuizResultResponse result = service.getResult(quizId);

        assertThat(result.score()).isEqualTo(50);
        assertThat(result.total()).isEqualTo(2);
    }
}
