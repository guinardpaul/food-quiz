package com.guinardsolutions.foodquiz.infrastructure.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public abstract class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    protected String label;

    @Column(name = "image_url")
    protected String imageUrl;

    protected QuestionEntity() {
    }

    protected QuestionEntity(Long id, String label) {
        this(id, label, null);
    }

    protected QuestionEntity(Long id, String label, String imageUrl) {
        this.id = id;
        this.label = label;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

}
