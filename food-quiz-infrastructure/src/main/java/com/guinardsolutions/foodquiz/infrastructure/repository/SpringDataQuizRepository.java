package com.guinardsolutions.foodquiz.infrastructure.repository;

import com.guinardsolutions.foodquiz.infrastructure.entity.QuizEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpringDataQuizRepository extends JpaRepository<QuizEntity, Long> {

    @Query(value = "SELECT * from quiz ORDER BY random() limit 1", nativeQuery = true)
    Optional<QuizEntity> findRandomQuiz();
}
