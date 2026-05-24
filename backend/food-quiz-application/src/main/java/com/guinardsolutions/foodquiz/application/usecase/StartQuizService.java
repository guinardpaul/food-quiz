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

    private static final int MAX_QUESTIONS = 20;

    private final QuizRepository quizRepository;
    private final QuizSessionRepository sessionRepository;
    private final QuizResponseMapper quizResponseMapper;

    public StartQuizService(QuizRepository quizRepository, QuizSessionRepository sessionRepository) {
        this.quizRepository = quizRepository;
        this.sessionRepository = sessionRepository;
        this.quizResponseMapper = new QuizResponseMapper();
    }

    @Override
    public QuizResponse startQuiz(int questionCount) {
        if (questionCount < 1 || questionCount > MAX_QUESTIONS) {
            throw new IllegalArgumentException("Question count must be between 1 and " + MAX_QUESTIONS);
        }

        Optional<Quiz> quiz = this.quizRepository.findRandomQuiz(questionCount);
        if (quiz.isEmpty()) {
            throw new IllegalStateException("No questions available");
        }

        Quiz foundQuiz = quiz.get();
        sessionRepository.save(foundQuiz);
        return this.quizResponseMapper.toResponse(foundQuiz);
    }

}
