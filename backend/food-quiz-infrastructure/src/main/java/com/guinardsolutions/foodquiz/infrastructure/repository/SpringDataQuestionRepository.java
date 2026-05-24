package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.infrastructure.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataQuestionRepository extends JpaRepository<QuestionEntity, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY random() LIMIT :count", nativeQuery = true)
    List<QuestionEntity> findRandomQuestions(@Param("count") int count);
}
