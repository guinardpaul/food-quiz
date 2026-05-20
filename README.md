[![Java CI](https://github.com/guinardpaul/food-quiz/actions/workflows/ci.yml/badge.svg)](https://github.com/guinardpaul/food-quiz/actions/workflows/ci.yml)
[![CodeQL](https://github.com/guinardpaul/food-quiz/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/guinardpaul/food-quiz/actions/workflows/github-code-scanning/codeql)
[![Dependency review](https://github.com/guinardpaul/food-quiz/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/guinardpaul/food-quiz/actions/workflows/dependency-review.yml)
# Food Quiz server

## Description

Quiz game focused on learning and training 
on Functional Insulin Therapy regarding 
food weight and glucid quantity estimations.

## Stack

- Spring Boot
- REST
- PostgreSQL
- Liquibase

## Architecture

This project uses a multi-module architecture composed of :
- api
- application
- domain
- infrastructure

Development coherence is assured by the architecture-tests module using ArchUnit.

JaCoCo aggregate tests report is realized within tests-report module.

## Installation

### Pre requisites

- JDK 25
- Apache Maven > 3.8

### Build project

Run the following command:

`mvn clean install`

## Deployment

TODO

## Initialisation des données d'entraînement

Les quiz d'entraînement sont initialisés par Liquibase au démarrage de l'application. Le seed utilise de vraies photos de plats issues d'une source externe (TheMealDB) et associe à chaque assiette une estimation du poids de chaque aliment visible.

Le projet `food-quiz-data-initializer` sert à générer automatiquement le changelog de seed depuis le manifeste `food-quiz-data-initializer/data/real_plate_sources.json` :

`python3 food-quiz-data-initializer/scripts/generate_seed.py`

Par défaut, le script interroge l'API TheMealDB pour récupérer les URLs des vraies images de plats. En environnement sans accès réseau, il peut régénérer le même changelog avec les URLs réelles de secours du manifeste :

`python3 food-quiz-data-initializer/scripts/generate_seed.py --offline`

Le script produit `food-quiz-bootstrap/src/main/resources/db/changelog/002_seed_real_food_quizzes.xml`. Ce changelog insère les quiz, les questions de poids par aliment, la question de glucides totaux par assiette et les estimations détaillées dans `plate_food_items`.
