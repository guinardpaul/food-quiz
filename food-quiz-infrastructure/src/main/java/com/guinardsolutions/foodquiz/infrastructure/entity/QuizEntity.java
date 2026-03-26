package com.guinardsolutions.foodquiz.infrastructure.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "quiz")
public class QuizEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "quiz_id", unique = true, nullable = false)
    private String quizId;

    @OneToMany(cascade = CascadeType.DETACH, orphanRemoval = true)
    private List<QuestionEntity> questions;

    public QuizEntity(long id, String quizId, List<QuestionEntity> questions) {
        this.id = id;
        this.quizId = quizId;
        this.questions = questions;
    }

    public long getId() {
        return id;
    }

    public String getQuizId() {
        return quizId;
    }

    public List<QuestionEntity> getQuestions() {
        return questions;
    }
}
