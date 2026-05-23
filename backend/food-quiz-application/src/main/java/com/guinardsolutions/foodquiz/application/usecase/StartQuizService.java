package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.mapper.QuizResponseMapper;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizRepository;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StartQuizService implements StartQuizUseCase{

    private final QuizRepository quizRepository;
    private final QuizResponseMapper quizResponseMapper;

    public StartQuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
        this.quizResponseMapper = new QuizResponseMapper();
    }

    @Override
    public QuizResponse startQuiz() {
        Optional<Quiz> quiz = this.quizRepository.findRandomQuiz();
        if (quiz.isEmpty()) {
            throw new IllegalStateException("No quiz found");
        }

        return this.quizResponseMapper.toResponse(quiz.get());
    }

}
