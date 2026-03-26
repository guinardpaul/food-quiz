package com.guinardsolutions.foodquiz.api.controller;

import com.guinardsolutions.foodquiz.api.QuizMapper;
import com.guinardsolutions.foodquiz.api.dto.QuizDto;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final StartQuizUseCase startQuizUseCase;
    private final QuizMapper quizMapper;

    public QuizController(StartQuizUseCase startQuizUseCase) {
        this.startQuizUseCase = startQuizUseCase;
        this.quizMapper = new QuizMapper();
    }

    @GetMapping(
            name = "Start a Quiz",
            path = "/start",
    produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizDto> startQuiz() {
        return ResponseEntity.ok(quizMapper.toDto(startQuizUseCase.startQuiz()));
    }
}
