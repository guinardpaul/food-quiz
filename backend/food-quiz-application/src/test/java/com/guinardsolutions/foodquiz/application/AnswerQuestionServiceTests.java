package com.guinardsolutions.foodquiz.application;

import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.application.usecase.AnswerQuestionService;
import com.guinardsolutions.foodquiz.application.usecase.AnswerQuestionUseCase;
import com.guinardsolutions.foodquiz.domain.ChoiceQuestion;
import com.guinardsolutions.foodquiz.domain.GlycemicImpact;
import com.guinardsolutions.foodquiz.domain.Quiz;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnswerQuestionServiceTests {

    private AnswerQuestionUseCase service;
    private QuizSessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        sessionRepository = mock(QuizSessionRepository.class);
        service = new AnswerQuestionService(sessionRepository);
    }

    @Test
    void should_throw_when_session_not_found() {
        when(sessionRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.answerQuestion("unknown", "56g"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_return_correct_feedback_when_answer_is_right() {
        String quizId = UUID.randomUUID().toString();
        ChoiceQuestion question = new ChoiceQuestion(
                "Combien de glucides ?", null, "Riz blanc", null,
                List.of("20g", "42g", "56g", "75g"), "56g",
                Map.of("breadSlices", 4, "sugarCubes", 14), GlycemicImpact.HIGH);
        Quiz quiz = new Quiz(quizId, List.of(question));
        quiz.currentQuestion();
        when(sessionRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        FeedbackResponse feedback = service.answerQuestion(quizId, "56g");

        assertThat(feedback.correct()).isTrue();
        assertThat(feedback.correctAnswer()).isEqualTo("56g");
        assertThat(feedback.glycemicImpact()).isEqualTo("HIGH");
        assertThat(feedback.hasNextQuestion()).isFalse();
    }

    @Test
    void should_return_incorrect_feedback_when_answer_is_wrong() {
        String quizId = UUID.randomUUID().toString();
        ChoiceQuestion question = new ChoiceQuestion(
                "Combien de glucides ?", null, "Riz blanc", null,
                List.of("20g", "42g", "56g", "75g"), "56g",
                Map.of("breadSlices", 4, "sugarCubes", 14), GlycemicImpact.HIGH);
        Quiz quiz = new Quiz(quizId, List.of(question));
        quiz.currentQuestion();
        when(sessionRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        FeedbackResponse feedback = service.answerQuestion(quizId, "20g");

        assertThat(feedback.correct()).isFalse();
        assertThat(feedback.correctAnswer()).isEqualTo("56g");
    }
}
