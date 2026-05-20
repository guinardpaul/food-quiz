package com.guinardsolutions.foodquiz.infrastructure.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
@DiscriminatorValue("CHOICE")
public class ChoiceQuestionEntity extends QuestionEntity {

    private List<String> proposedAnswer;
    private String correctAnswer;

    public ChoiceQuestionEntity() {
    }

    public ChoiceQuestionEntity(Long id, String label, List<String> proposedAnswer, String correctAnswer) {
        this(id, label, null, proposedAnswer, correctAnswer);
    }

    public ChoiceQuestionEntity(Long id, String label, String imageUrl, List<String> proposedAnswer, String correctAnswer) {
        super(id, label, imageUrl);
        this.proposedAnswer = proposedAnswer;
        this.correctAnswer = correctAnswer;
    }

    public List<String> getProposedAnswer() {
        return proposedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}
