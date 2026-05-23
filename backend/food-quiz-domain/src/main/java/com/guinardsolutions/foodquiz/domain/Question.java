package com.guinardsolutions.foodquiz.domain;


public abstract class Question {

    private final String label;
    protected boolean answered;

    public Question(String label) {
        this.label = label;
        this.answered = false;
    }

    public String getLabel() {
        return label;
    }

    abstract boolean answer(String answer);

    public boolean isAnswered() {
        return answered;
    }

}
