package com.guinardsolutions.foodquiz.infrastructure.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NUMBER")
public class NumberQuestionEntity extends QuestionEntity {

    private double expectedValue;
    private double tolerance;

    public NumberQuestionEntity() {
    }

    public NumberQuestionEntity(Long id, String label, double expectedValue, double tolerance) {
        super(id, label);
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getTolerance() {
        return tolerance;
    }
}
