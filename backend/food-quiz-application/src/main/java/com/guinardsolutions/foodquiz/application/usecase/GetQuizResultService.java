package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.mapper.QuizResultResponseMapper;
import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Service;

@Service
public class GetQuizResultService implements GetQuizResultUseCase {

    private final QuizSessionRepository sessionRepository;
    private final QuizResultResponseMapper mapper;

    public GetQuizResultService(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.mapper = new QuizResultResponseMapper();
    }

    @Override
    public QuizResultResponse getResult(String quizId) {
        Quiz quiz = sessionRepository.findById(quizId)
                .orElseThrow(() -> new IllegalStateException("Quiz session not found: " + quizId));
        return mapper.toResponse(quiz);
    }
}
