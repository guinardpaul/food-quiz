package com.guinardsolutions.foodquiz.infrastructure.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@DiscriminatorValue("CHOICE")
public class ChoiceQuestionEntity extends QuestionEntity {

    private String imageUrl;
    private String foodName;
    private String portionDescription;

    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> proposedAnswer;

    private String correctAnswer;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Integer> equivalents;

    private String glycemicImpact;

    public ChoiceQuestionEntity() {
    }

    public ChoiceQuestionEntity(Long id, String label, List<String> proposedAnswer, String correctAnswer) {
        super(id, label);
        this.proposedAnswer = proposedAnswer;
        this.correctAnswer = correctAnswer;
    }

    public ChoiceQuestionEntity(Long id, String label, String imageUrl, String foodName, String portionDescription,
                                List<String> proposedAnswer, String correctAnswer,
                                Map<String, Integer> equivalents, String glycemicImpact) {
        super(id, label);
        this.imageUrl = imageUrl;
        this.foodName = foodName;
        this.portionDescription = portionDescription;
        this.proposedAnswer = proposedAnswer;
        this.correctAnswer = correctAnswer;
        this.equivalents = equivalents;
        this.glycemicImpact = glycemicImpact;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getPortionDescription() {
        return portionDescription;
    }

    public List<String> getProposedAnswer() {
        return proposedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Map<String, Integer> getEquivalents() {
        return equivalents;
    }

    public String getGlycemicImpact() {
        return glycemicImpact;
    }
}
