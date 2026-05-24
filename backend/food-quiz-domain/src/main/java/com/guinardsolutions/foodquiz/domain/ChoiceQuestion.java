package com.guinardsolutions.foodquiz.domain;

import java.util.List;
import java.util.Map;

public class ChoiceQuestion extends Question {

    private final String imageUrl;
    private final String foodName;
    private final String portionDescription;
    private final List<String> proposedAnswers;
    private final String correctAnswer;
    private final Map<String, Integer> equivalents;
    private final GlycemicImpact glycemicImpact;

    public ChoiceQuestion(String label, List<String> proposedAnswers, String correctAnswer) {
        this(label, null, null, null, proposedAnswers, correctAnswer, null, null);
    }

    public ChoiceQuestion(String label, String imageUrl, String foodName, String portionDescription,
                          List<String> proposedAnswers, String correctAnswer,
                          Map<String, Integer> equivalents, GlycemicImpact glycemicImpact) {
        super(label);
        this.imageUrl = imageUrl;
        this.foodName = foodName;
        this.portionDescription = portionDescription;
        this.proposedAnswers = proposedAnswers;
        this.correctAnswer = correctAnswer;
        this.equivalents = equivalents;
        this.glycemicImpact = glycemicImpact;
    }

    @Override
    boolean answer(String answer) {
        this.answered = true;
        return this.correctAnswer.equals(answer);
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

    public List<String> getProposedAnswers() {
        return proposedAnswers;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public Map<String, Integer> getEquivalents() {
        return equivalents;
    }

    public GlycemicImpact getGlycemicImpact() {
        return glycemicImpact;
    }
}
