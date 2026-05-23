package com.guinardsolutions.foodquiz.api.controller;

import com.guinardsolutions.foodquiz.api.dto.AnswerRequestDto;
import com.guinardsolutions.foodquiz.api.dto.FeedbackDto;
import com.guinardsolutions.foodquiz.api.dto.QuestionDto;
import com.guinardsolutions.foodquiz.api.dto.QuizDto;
import com.guinardsolutions.foodquiz.api.dto.QuizResultDto;
import com.guinardsolutions.foodquiz.api.mapper.FeedbackMapper;
import com.guinardsolutions.foodquiz.api.mapper.QuestionMapper;
import com.guinardsolutions.foodquiz.api.mapper.QuizMapper;
import com.guinardsolutions.foodquiz.api.mapper.QuizResultMapper;
import com.guinardsolutions.foodquiz.application.usecase.AnswerQuestionUseCase;
import com.guinardsolutions.foodquiz.application.usecase.GetCurrentQuestionUseCase;
import com.guinardsolutions.foodquiz.application.usecase.GetQuizResultUseCase;
import com.guinardsolutions.foodquiz.application.usecase.StartQuizUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    private final StartQuizUseCase startQuizUseCase;
    private final GetCurrentQuestionUseCase getCurrentQuestionUseCase;
    private final AnswerQuestionUseCase answerQuestionUseCase;
    private final GetQuizResultUseCase getQuizResultUseCase;

    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;
    private final FeedbackMapper feedbackMapper;
    private final QuizResultMapper quizResultMapper;

    public QuizController(StartQuizUseCase startQuizUseCase,
                          GetCurrentQuestionUseCase getCurrentQuestionUseCase,
                          AnswerQuestionUseCase answerQuestionUseCase,
                          GetQuizResultUseCase getQuizResultUseCase) {
        this.startQuizUseCase = startQuizUseCase;
        this.getCurrentQuestionUseCase = getCurrentQuestionUseCase;
        this.answerQuestionUseCase = answerQuestionUseCase;
        this.getQuizResultUseCase = getQuizResultUseCase;
        this.quizMapper = new QuizMapper();
        this.questionMapper = new QuestionMapper();
        this.feedbackMapper = new FeedbackMapper();
        this.quizResultMapper = new QuizResultMapper();
    }

    @GetMapping(name = "Start a Quiz", path = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizDto> startQuiz() {
        return ResponseEntity.ok(quizMapper.toDto(startQuizUseCase.startQuiz()));
    }

    @GetMapping(name = "Get current question", path = "/{quizId}/question", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuestionDto> getCurrentQuestion(@PathVariable String quizId) {
        return ResponseEntity.ok(questionMapper.toDto(getCurrentQuestionUseCase.getCurrentQuestion(quizId)));
    }

    @PostMapping(name = "Submit answer", path = "/{quizId}/answer",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FeedbackDto> answerQuestion(@PathVariable String quizId,
                                                      @RequestBody AnswerRequestDto request) {
        return ResponseEntity.ok(feedbackMapper.toDto(answerQuestionUseCase.answerQuestion(quizId, request.answer())));
    }

    @GetMapping(name = "Get quiz result", path = "/{quizId}/result", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<QuizResultDto> getResult(@PathVariable String quizId) {
        return ResponseEntity.ok(quizResultMapper.toDto(getQuizResultUseCase.getResult(quizId)));
    }
}
