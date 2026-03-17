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
