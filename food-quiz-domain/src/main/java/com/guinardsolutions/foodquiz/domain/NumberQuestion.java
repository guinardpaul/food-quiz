package com.guinardsolutions.foodquiz.domain;

public class NumberQuestion extends Question {

    private final double expectedValue;
    private final double tolerance;

    public NumberQuestion(String label, double expectedValue, double tolerance) {
        super(label);
        this.expectedValue = expectedValue;
        this.tolerance = tolerance;
    }

    @Override
    boolean answer(String answer) {
        double number;
        try {
            number = Double.parseDouble(answer);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Answer is not a number");
        }

        return Math.abs(expectedValue - number) <= tolerance;
    }
}
