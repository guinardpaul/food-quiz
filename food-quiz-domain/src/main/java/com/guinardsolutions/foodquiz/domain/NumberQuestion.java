package com.guinardsolutions.foodquiz.domain;

public class NumberQuestion extends Question {

    private final double expectedValue;
    private final double tolerance;

    public NumberQuestion(String label, double expectedValue, double tolerance) {
        this(label, null, expectedValue, tolerance);
    }

    public NumberQuestion(String label, String imageUrl, double expectedValue, double tolerance) {
        super(label, imageUrl);
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

        this.answered = true;
        return Math.abs(expectedValue - number) <= tolerance;
    }
}
