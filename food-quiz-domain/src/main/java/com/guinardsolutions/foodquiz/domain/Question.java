package com.guinardsolutions.foodquiz.domain;


public abstract class Question {

    private final String label;
    private final String imageUrl;
    protected boolean answered;

    public Question(String label) {
        this(label, null);
    }

    public Question(String label, String imageUrl) {
        this.label = label;
        this.imageUrl = imageUrl;
        this.answered = false;
    }

    public String getLabel() {
        return label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    abstract boolean answer(String answer);

    public boolean isAnswered() {
        return answered;
    }

}
