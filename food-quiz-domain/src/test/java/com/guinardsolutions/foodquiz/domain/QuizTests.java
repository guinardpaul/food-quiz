package com.guinardsolutions.foodquiz.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuizTests {

    @Test
    void should_start_a_quiz_and_return_first_question() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "A")));

        Question question = quiz.currentQuestion();
        assertThat(question.getLabel()).isEqualTo("Q1");
    }

    @Test
    void should_have_at_least_one_question() {
        List<Question> questions = List.of();
        assertThatThrownBy(() -> new Quiz(questions))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("A Quiz must have at least one question");
    }

    @Test
    void should_accept_correct_answer() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "A")));
        quiz.currentQuestion();

        boolean result = quiz.answer("A");
        assertThat(result).isTrue();
    }

    @Test
    void should_refuse_incorrect_answer() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "B")));
        quiz.currentQuestion();

        boolean result = quiz.answer("A");
        assertThat(result).isFalse();
    }

    @Test
    void should_get_next_question_after_answering() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "B"))
        );


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
    void should_not_allow_to_get_next_question_if_current_is_not_answered() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "B")
        ));

        quiz.currentQuestion();
        assertThatThrownBy(quiz::currentQuestion)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot get next question because current has not been answered");
    }

    @Test
    void should_know_if_there_is_questions_left() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "B")
        ));

        boolean res = quiz.hasQuestionLeft();
        assertThat(res).isTrue();
        quiz.currentQuestion();
        quiz.answer("B");

        res = quiz.hasQuestionLeft();
        assertThat(res).isTrue();
        quiz.currentQuestion();
        quiz.answer("B");

        res = quiz.hasQuestionLeft();
        assertThat(res).isFalse();
    }

    @Test
    void should_know_when_theres_no_question_left() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "A")));

        quiz.currentQuestion();
        assertThatThrownBy(quiz::currentQuestion)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("There is no question left");
    }

    @Test
    void should_know_proposed_answers_when_getting_a_question() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "A")));
        Question q = quiz.currentQuestion();

        List<String> proposedAnswers = ((ChoiceQuestion) q).getProposedAnswers();
        assertThat(proposedAnswers)
                .hasSize(2)
                .contains("A")
                .contains("B");
    }

    @Test
    void cannot_get_score_if_quiz_is_not_finished_question_not_answered() {
        Quiz quiz = new Quiz(List.of(createChoiceQuestion("Q1", List.of("A","B"), "A")));
        quiz.currentQuestion();

        assertThatThrownBy(quiz::getScore)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("All quiz questions must be answered before getting the score");
    }

    @Test
    void cannot_get_score_if_quiz_is_not_finished_question_left() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "A")
        ));
        quiz.currentQuestion();
        quiz.answer("A");

        assertThatThrownBy(quiz::getScore)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("All quiz questions must be answered before getting the score");
    }

    @Test
    void should_get_score_when_quiz_end() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "B"),
                createChoiceQuestion("Q3", List.of("A","B"), "A")
        ));

        quiz.currentQuestion();
        quiz.answer("B");
        quiz.currentQuestion();
        quiz.answer("B");
        quiz.currentQuestion();
        quiz.answer("B");
        Boolean res = quiz.hasQuestionLeft();
        assertThat(res).isFalse();

        double score = quiz.getScore();
        assertThat(score).isEqualTo(33.0);
    }

    @Test
    void should_get_quiz_review_at_the_end() {
        Quiz quiz = new Quiz(List.of(
                createChoiceQuestion("Q1", List.of("A","B"), "A"),
                createChoiceQuestion("Q2", List.of("A","B"), "B"),
                createChoiceQuestion("Q3", List.of("A","B"), "A")
        ));
        quiz.currentQuestion();
        quiz.answer("A");
        quiz.currentQuestion();
        quiz.answer("B");
        quiz.currentQuestion();
        quiz.answer("B");
        Boolean res = quiz.hasQuestionLeft();
        assertThat(res).isFalse();

        QuizReview review = quiz.getQuizReview();
        assertThat(review.getQuestionReview()).hasSize(3);
        List<QuestionReview> correctAnswers = review.getQuestionReview().stream().filter(QuestionReview::isCorrect).toList();
        assertThat(correctAnswers).hasSize(2);
    }

    @Test
    void should_throw_exception_if_numberquestion_answer_is_not_a_number() {
        Quiz quiz = new Quiz(List.of(createNumberQuestion()));

        quiz.currentQuestion();
        assertThatThrownBy(() -> quiz.answer("NotANumber"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a number");
    }

    @Test
    void should_numberQuestion_be_correct_given_a_tolerance() {
        Quiz quiz = new Quiz(List.of(createNumberQuestion()));

        quiz.currentQuestion();
        boolean res = quiz.answer("17");
        assertThat(res).isTrue();
    }

    @Test
    void should_numberQuestion_be_incorrect() {
        Quiz quiz = new Quiz(List.of(createNumberQuestion()));

        quiz.currentQuestion();
        boolean res = quiz.answer("15");
        assertThat(res).isFalse();
    }

    private Question createChoiceQuestion(String label, List<String> answers, String correctAnswer) {
        return new ChoiceQuestion(label, answers, correctAnswer);
    }

    private Question createNumberQuestion() {
        return new NumberQuestion("Q1", 20, 3);
    }
}
