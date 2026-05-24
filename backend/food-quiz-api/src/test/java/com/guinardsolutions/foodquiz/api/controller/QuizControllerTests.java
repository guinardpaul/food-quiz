package com.guinardsolutions.foodquiz.api.controller;

import com.guinardsolutions.foodquiz.api.TestConfig;
import com.guinardsolutions.foodquiz.application.port.in.FeedbackResponse;
import com.guinardsolutions.foodquiz.application.port.in.QuestionResponse;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.port.in.QuizResultResponse;
import com.guinardsolutions.foodquiz.application.usecase.AnswerQuestionUseCase;
import com.guinardsolutions.foodquiz.application.usecase.GetCurrentQuestionUseCase;
import com.guinardsolutions.foodquiz.application.usecase.GetQuizResultUseCase;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuizController.class)
@ContextConfiguration(classes = TestConfig.class)
class QuizControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StartQuizUseCase startQuizUseCase;

    @MockitoBean
    private GetCurrentQuestionUseCase getCurrentQuestionUseCase;

    @MockitoBean
    private AnswerQuestionUseCase answerQuestionUseCase;

    @MockitoBean
    private GetQuizResultUseCase getQuizResultUseCase;

    @Test
    void should_return_quizDto_when_starting_quiz_with_default_count() throws Exception {
        String id = UUID.randomUUID().toString();
        when(startQuizUseCase.startQuiz(5)).thenReturn(new QuizResponse(id));

        mockMvc.perform(get("/quiz/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(id));
    }

    @Test
    void should_pass_count_param_to_use_case() throws Exception {
        String id = UUID.randomUUID().toString();
        when(startQuizUseCase.startQuiz(10)).thenReturn(new QuizResponse(id));

        mockMvc.perform(get("/quiz/start").param("count", "10"))
                .andExpect(status().isOk());

        verify(startQuizUseCase).startQuiz(10);
    }

    @Test
    void should_return_404_when_no_questions_available() throws Exception {
        when(startQuizUseCase.startQuiz(5)).thenThrow(new IllegalStateException("No questions available"));

        mockMvc.perform(get("/quiz/start"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("No questions available"));
    }

    @Test
    void should_return_400_when_count_is_invalid() throws Exception {
        when(startQuizUseCase.startQuiz(25)).thenThrow(new IllegalArgumentException("Question count must be between 1 and 20"));

        mockMvc.perform(get("/quiz/start").param("count", "25"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Question count must be between 1 and 20"));
    }

    @Test
    void should_return_question_for_given_quiz() throws Exception {
        String quizId = UUID.randomUUID().toString();
        QuestionResponse response = new QuestionResponse("Combien de glucides ?", "Riz blanc",
                "https://img.example.com/riz.jpg", "Portion moyenne (~180g)",
                List.of("20g", "42g", "56g", "75g"));
        when(getCurrentQuestionUseCase.getCurrentQuestion(quizId)).thenReturn(response);

        mockMvc.perform(get("/quiz/{quizId}/question", quizId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodName").value("Riz blanc"))
                .andExpect(jsonPath("$.proposedAnswers[0]").value("20g"));
    }

    @Test
    void should_return_feedback_after_answering() throws Exception {
        String quizId = UUID.randomUUID().toString();
        FeedbackResponse feedback = new FeedbackResponse(true, "56g",
                Map.of("breadSlices", 4, "sugarCubes", 14), "HIGH", false);
        when(answerQuestionUseCase.answerQuestion(quizId, "56g")).thenReturn(feedback);

        mockMvc.perform(post("/quiz/{quizId}/answer", quizId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"answer\":\"56g\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.correctAnswer").value("56g"))
                .andExpect(jsonPath("$.glycemicImpact").value("HIGH"));
    }

    @Test
    void should_return_result_when_quiz_is_finished() throws Exception {
        String quizId = UUID.randomUUID().toString();
        when(getQuizResultUseCase.getResult(quizId)).thenReturn(new QuizResultResponse(75, 4));

        mockMvc.perform(get("/quiz/{quizId}/result", quizId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(75))
                .andExpect(jsonPath("$.total").value(4));
    }
}
