package com.guinardsolutions.foodquiz.api.controller;

import com.guinardsolutions.foodquiz.api.TestConfig;
import com.guinardsolutions.foodquiz.application.port.in.QuizResponse;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = QuizController.class)
@ContextConfiguration(classes = TestConfig.class)
class QuizControllerTests {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private StartQuizUseCase startQuizUseCase;

    @Test
    void should_return_quizDto_when_starting_quiz() throws Exception {
        String id = UUID.randomUUID().toString();
        QuizResponse quizResponse = new QuizResponse(id);
        when(startQuizUseCase.startQuiz()).thenReturn(quizResponse);

        mockMvc.perform(get("/quiz/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quizId").value(id));
    }
}
