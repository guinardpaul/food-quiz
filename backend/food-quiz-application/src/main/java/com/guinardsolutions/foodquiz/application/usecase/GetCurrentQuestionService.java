package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.mapper.QuestionResponseMapper;
import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentQuestionService implements GetCurrentQuestionUseCase {

    private final QuizSessionRepository sessionRepository;
    private final QuestionResponseMapper mapper;

    public GetCurrentQuestionService(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.mapper = new QuestionResponseMapper();
    }

    @Override
    public QuestionResponse getCurrentQuestion(String quizId) {
        Quiz quiz = sessionRepository.findById(quizId)
                .orElseThrow(() -> new IllegalStateException("Quiz session not found: " + quizId));
        return mapper.toResponse(quiz.currentQuestion());
    }
}
