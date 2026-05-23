package com.guinardsolutions.foodquiz.architecture;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class DependencyDirectionTests extends ArchitectureTestsConfig {

    @Test
    void api_should_not_depend_on_infrastructure_or_domain() {
        noClasses()
                .that().resideInAPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..domain..")
                .check(classes);
    }

    @Test
    void domain_should_not_depend_on_any_other_layer() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..api..", "..application..", "..infrastructure..")
                .check(classes);
    }

    @Test
    void infrastructure_should_not_depend_on_api() {
        noClasses()
                .that().resideInAPackage("..infrastructure..")
                .should().dependOnClassesThat()
                .resideInAPackage("..api..")
                .check(classes);
    }

    @Test
    void use_cases_should_be_records() {
        classes()
                .that().resideInAPackage("..application.port.in..")
                .should().beRecords()
                .check(classes);
    }

    @Test
    void api_dtos_should_not_be_used_outside_api_layer() {
        noClasses()
                .that().resideOutsideOfPackage("..api..")
                .should().dependOnClassesThat()
                .resideInAPackage("..api.dto..")
                .check(classes);
    }

    @Test
    void controllers_should_be_annotated_with_rest_controller() {
        classes()
                .that().resideInAPackage("..api.controller..")
                .should().beAnnotatedWith(RestController.class)
                .check(classes);
    }

    @Test
    void clean_architecture_layers_should_be_respected() {
        layeredArchitecture().consideringAllDependencies()
                .layer("API").definedBy("..api..")
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..infrastructure..")

                .whereLayer("API").mayOnlyBeAccessedByLayers("Infrastructure")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("API", "Infrastructure")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")

                .check(classes);
    }

}

