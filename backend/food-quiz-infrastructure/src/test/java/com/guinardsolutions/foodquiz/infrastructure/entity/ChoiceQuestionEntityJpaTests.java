package com.guinardsolutions.foodquiz.infrastructure.entity;

import com.guinardsolutions.foodquiz.infrastructure.repository.SpringDataQuestionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.test.database.replace=NONE",
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ChoiceQuestionEntityJpaTests {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SpringDataQuestionRepository repository;

    @Test
    void should_persist_and_reload_json_fields() {
        var entity = new ChoiceQuestionEntity(
                null, "Quelle est la bonne portion ?",
                "https://img.example.com/pomme.jpg", "Pomme", "1 portion standard",
                List.of("100g", "150g", "200g"), "150g",
                Map.of("Banane", 120, "Pain blanc", 30),
                "Modéré"
        );

        repository.save(entity);
        entityManager.flush();
        entityManager.clear();

        var saved = (ChoiceQuestionEntity) repository.findById(entity.getId()).orElseThrow();

        assertThat(saved.getProposedAnswer()).containsExactlyInAnyOrder("100g", "150g", "200g");
        assertThat(saved.getEquivalents())
                .containsEntry("Banane", 120)
                .containsEntry("Pain blanc", 30);
    }
}
