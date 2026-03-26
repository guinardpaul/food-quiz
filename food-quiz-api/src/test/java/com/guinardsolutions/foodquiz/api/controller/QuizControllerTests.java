package com.guinardsolutions.foodquiz.api.controller;

import com.guinardsolutions.foodquiz.api.dto.QuizDto;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizControllerTests {

    private QuizController quizController;
    private StartQuizUseCase startQuizUseCase;

    @BeforeEach
    void setup() {
        startQuizUseCase = mock(StartQuizUseCase.class);
        quizController = new QuizController(startQuizUseCase);
    }

    @Test
    void should_return_quizDto_when_starting_quiz() {
        QuizResponse quizResponse = new QuizResponse(UUID.randomUUID().toString());
        when(startQuizUseCase.startQuiz()).thenReturn(quizResponse);

        ResponseEntity<QuizDto> response = quizController.startQuiz();
    }
}
