package com.guinardsolutions.foodquiz.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuizTests {

    @Test
    void should_start_a_quiz_and_return_first_question() {
        Quiz quiz = new Quiz(List.of(new Question("Q1", List.of("A"), "A")));

        Question question = quiz.currentQuestion();
        assertThat(question.getLabel()).isEqualTo("Q1");
    }

    @Test
    void should_have_at_least_one_question() {
        assertThatThrownBy(() -> new Quiz(new ArrayList<>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A Quiz must have at least one question");
    }

    @Test
    void should_accept_correct_answer() {
        Quiz quiz = new Quiz(List.of(new Question("Q1", List.of("A"), "A")));
        quiz.currentQuestion();

        boolean result = quiz.answer("A");
        assertThat(result).isTrue();
    }

    @Test
    void should_refuse_incorrect_answer() {
        Quiz quiz = new Quiz(List.of(new Question("Q1", List.of("A", "B"), "B")));
        quiz.currentQuestion();

        boolean result = quiz.answer("A");
        assertThat(result).isFalse();
    }

    @Test
    void should_get_next_question_after_answering() {
        Quiz quiz = new Quiz(List.of(new Question("Q1", List.of("A", "B"), "A"), new Question("Q2", List.of("A", "B"), "B")));


        Question q1 = quiz.currentQuestion();
        assertThat(q1.getLabel()).isEqualTo("Q1");
        boolean result = quiz.answer("A");
        assertThat(result).isTrue();

        Question q2 = quiz.currentQuestion();
        assertThat(q2.getLabel()).isEqualTo("Q2");
        result = quiz.answer("A");
        assertThat(result).isFalse();
    }

    @Test
    void should_know_when_theres_no_question_left() {
        Quiz quiz = new Quiz(List.of(new Question("Q1", List.of("A", "B"), "A")));

        quiz.currentQuestion();
        assertThatThrownBy(() -> quiz.currentQuestion())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("There is no question left");
    }

}
