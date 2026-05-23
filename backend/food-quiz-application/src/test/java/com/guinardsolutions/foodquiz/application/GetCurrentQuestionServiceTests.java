package com.guinardsolutions.foodquiz.application;

import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;
import com.guinardsolutions.foodquiz.application.port.out.QuizSessionRepository;
import com.guinardsolutions.foodquiz.application.usecase.GetCurrentQuestionService;
import com.guinardsolutions.foodquiz.application.usecase.GetCurrentQuestionUseCase;
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
class GetCurrentQuestionServiceTests {

    private GetCurrentQuestionUseCase service;
    private QuizSessionRepository sessionRepository;

    @BeforeEach
    void setup() {
        sessionRepository = mock(QuizSessionRepository.class);
        service = new GetCurrentQuestionService(sessionRepository);
    }

    @Test
    void should_throw_when_session_not_found() {
        when(sessionRepository.findById("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCurrentQuestion("unknown"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_return_question_response_for_choice_question() {
        String quizId = UUID.randomUUID().toString();
        ChoiceQuestion question = new ChoiceQuestion(
                "Combien de glucides ?", "https://img.example.com/riz.jpg",
                "Riz blanc", "Portion moyenne (~180g)",
                List.of("20g", "42g", "56g", "75g"), "56g",
                Map.of("breadSlices", 4, "sugarCubes", 14), GlycemicImpact.HIGH);
        Quiz quiz = new Quiz(quizId, List.of(question));
        when(sessionRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        QuestionResponse response = service.getCurrentQuestion(quizId);

        assertThat(response.foodName()).isEqualTo("Riz blanc");
        assertThat(response.imageUrl()).isEqualTo("https://img.example.com/riz.jpg");
        assertThat(response.portionDescription()).isEqualTo("Portion moyenne (~180g)");
        assertThat(response.proposedAnswers()).containsExactly("20g", "42g", "56g", "75g");
    }
}
