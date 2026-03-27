package com.guinardsolutions.foodquiz.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.guinardsolutions.foodquiz")
@EnableJpaRepositories(basePackages = "com.guinardsolutions.foodquiz.infrastructure")
@EntityScan(basePackages = "com.guinardsolutions.foodquiz.infrastructure")
public class FoodQuizApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodQuizApplication.class, args);
	}

}
