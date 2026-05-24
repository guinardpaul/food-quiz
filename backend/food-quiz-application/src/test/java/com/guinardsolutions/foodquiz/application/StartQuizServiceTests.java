package com.guinardsolutions.foodquiz.application;

import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizRepository;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizService;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StartQuizServiceTests {

    private StartQuizUseCase service;
    private QuizRepository quizRepository;
    private QuizSessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        quizRepository = mock(QuizRepository.class);
        sessionRepository = mock(QuizSessionRepository.class);
        service = new StartQuizService(quizRepository, sessionRepository);
    }

    @Test
    void should_throw_exception_when_no_quiz_found_in_repository() {
        when(quizRepository.findRandomQuiz()).thenReturn(Optional.empty());

        assertThatThrownBy(service::startQuiz)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No quiz found");
    }

    @Test
    void should_return_quizId_when_quiz_is_found() {
        Quiz quiz = new Quiz(UUID.randomUUID().toString(),
                List.of(new ChoiceQuestion("Q1", List.of("20g", "40g"), "40g")));
        when(quizRepository.findRandomQuiz()).thenReturn(Optional.of(quiz));

        QuizResponse response = service.startQuiz();

        assertThat(response.quizId()).isEqualTo(quiz.getQuizId().toString());
    }

    @Test
    void should_save_quiz_in_session_when_started() {
        Quiz quiz = new Quiz(UUID.randomUUID().toString(),
                List.of(new ChoiceQuestion("Q1", List.of("20g", "40g"), "40g")));
        when(quizRepository.findRandomQuiz()).thenReturn(Optional.of(quiz));

        service.startQuiz();

        verify(sessionRepository).save(any(Quiz.class));
    }
}
