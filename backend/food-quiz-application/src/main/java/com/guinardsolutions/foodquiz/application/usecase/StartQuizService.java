package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.mapper.QuizResponseMapper;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizRepository;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StartQuizService implements StartQuizUseCase {

    private final QuizRepository quizRepository;
    private final QuizSessionRepository sessionRepository;
    private final QuizResponseMapper quizResponseMapper;

    public StartQuizService(QuizRepository quizRepository, QuizSessionRepository sessionRepository) {
        this.quizRepository = quizRepository;
        this.sessionRepository = sessionRepository;
        this.quizResponseMapper = new QuizResponseMapper();
    }

    @Override
    public QuizResponse startQuiz() {
        Optional<Quiz> quiz = this.quizRepository.findRandomQuiz();
        if (quiz.isEmpty()) {
            throw new IllegalStateException("No quiz found");
        }

        Quiz foundQuiz = quiz.get();
        sessionRepository.save(foundQuiz);
        return this.quizResponseMapper.toResponse(foundQuiz);
    }

}
