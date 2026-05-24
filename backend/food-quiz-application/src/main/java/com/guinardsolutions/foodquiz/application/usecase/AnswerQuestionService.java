package com.guinardsolutions.foodquiz.application.usecase;

import com.guinardsolutions.foodquiz.application.mapper.FeedbackResponseMapper;
import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.Question;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.springframework.stereotype.Service;

@Service
public class AnswerQuestionService implements AnswerQuestionUseCase {

    private final QuizSessionRepository sessionRepository;
    private final FeedbackResponseMapper mapper;

    public AnswerQuestionService(QuizSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
        this.mapper = new FeedbackResponseMapper();
    }

    @Override
    public FeedbackResponse answerQuestion(String quizId, String answer) {
        Quiz quiz = sessionRepository.findById(quizId)
                .orElseThrow(() -> new IllegalStateException("Quiz session not found: " + quizId));

        boolean correct = quiz.answer(answer);
        Question question = quiz.peekCurrentQuestion();

        if (!(question instanceof ChoiceQuestion choiceQuestion)) {
            throw new IllegalStateException("Expected a ChoiceQuestion");
        }

        return mapper.toResponse(correct, choiceQuestion, quiz.hasQuestionLeft());
    }
}
