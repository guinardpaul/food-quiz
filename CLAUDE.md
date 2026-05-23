# Food Quiz — AI Assistant Guide

## Project Overview

Food Quiz is a Spring Boot REST server for a quiz game focused on learning Functional Insulin Therapy (food weight and glucid quantity estimation). The backend exposes a JSON API consumed by a frontend client.

- **Language**: Java 21
- **Framework**: Spring Boot 4.0.6
- **Build**: Apache Maven 3.8+ (multi-module)
- **Database**: PostgreSQL (production), H2 (tests)
- **Schema migrations**: Liquibase
- **Base package**: `com.guinardsolutions.foodquiz`

## Repository Structure

This is a monorepo with separate deployment targets:

```
food-quiz/
├── backend/        # Spring Boot API (deploy to Render)
├── frontend/       # Angular app (deploy to Vercel)
├── tools/          # Dev tooling (data generation scripts, etc.)
└── .github/
    └── workflows/  # CI workflows (path-filtered per sub-project)
```

## Module Structure

The backend is a Maven multi-module build under `backend/`. Each module maps to a clean architecture layer:

| Module | Role |
|---|---|
| `food-quiz-domain` | Core domain models and business logic — zero external dependencies |
| `food-quiz-application` | Use cases, input/output ports, application services |
| `food-quiz-infrastructure` | JPA entities, Spring Data repositories, adapters |
| `food-quiz-api` | REST controllers, DTOs, response mappers |
| `food-quiz-bootstrap` | Spring Boot entry point, `application.yaml`, Liquibase changelogs |
| `food-quiz-architecture-tests` | ArchUnit tests that enforce layer dependency rules |
| `food-quiz-tests-report` | JaCoCo aggregate coverage report across all modules |

> `food-quiz-docker` exists in the repository but is currently commented out of `backend/pom.xml`.

## Clean Architecture Rules (Enforced by ArchUnit)

These rules are verified by `DependencyDirectionTests` in `food-quiz-architecture-tests`. Violations cause test failures in CI.

- **Domain** has no dependency on any other layer (api, application, infrastructure).
- **Application** may only depend on Domain.
- **Infrastructure** may depend on Domain and Application, but not on API.
- **API** may only depend on Application — never on Infrastructure or Domain directly.
- **API DTOs** (`..api.dto..`) must not be used outside the API layer.
- **Controllers** in `..api.controller..` must be annotated with `@RestController`.
- **Input ports** in `..application.port.in..` must be Java records.

## Key Conventions

### Java Records
Use records for all immutable data crossing a layer boundary:
- Input ports (`port/in/`): e.g., `QuizResponse`
- Value objects in domain: e.g., `QuizId`
- API DTOs: e.g., `QuizDto`

### Explicit Mapper Classes
Every layer boundary has a dedicated mapper class. Do not use reflection-based mapping frameworks (e.g., MapStruct, ModelMapper). Write plain Java mappers:
- `food-quiz-infrastructure` → `QuizMapper` (Entity → Domain)
- `food-quiz-application` → `QuizResponseMapper` (Domain → Response)
- `food-quiz-api` → `QuizMapper` (Response → DTO)

### Dependency Injection
Use constructor injection exclusively. Do not use `@Autowired` on fields or setters.

### Domain Validation
Validate invariants in domain constructors and methods using `IllegalArgumentException` (invalid input) or `IllegalStateException` (illegal state transitions). Do not use Spring validation annotations in the domain layer.

### Null Safety
Return `Optional<T>` from repository methods instead of nullable references.

## Package Layout (per module)

```
com.guinardsolutions.foodquiz
├── domain/               # Entities, value objects, domain logic
├── application/
│   ├── port/
│   │   ├── in/           # Input port records (use case contracts)
│   │   └── out/          # Output port interfaces (e.g., QuizRepository)
│   ├── usecase/          # @Service implementations of input ports
│   └── mapper/           # Domain → Response mappers
├── infrastructure/
│   ├── entity/           # JPA entities
│   ├── repository/       # Spring Data JPA adapters
│   └── mapper/           # Entity → Domain mappers
└── api/
    ├── controller/       # @RestController classes
    ├── dto/              # API response records
    └── mapper/           # Response → DTO mappers
```

## Adding a New Feature

Follow this pattern for each new use case (example: `SubmitAnswer`):

1. **Domain** — add or extend domain models if needed (`food-quiz-domain`)
2. **Application / port/in** — create a new record for the use case response (`SubmitAnswerResponse.java`)
3. **Application / port/out** — add or extend repository interfaces if new data access is needed
4. **Application / usecase** — implement `@Service` class with constructor-injected ports
5. **Application / mapper** — add mapper from domain to the new response record
6. **Infrastructure** — implement any new port/out interfaces; add JPA entities/queries if needed
7. **API / dto** — create a new record for the HTTP response (`SubmitAnswerDto.java`)
8. **API / mapper** — map from application response record to API DTO
9. **API / controller** — add endpoint to an existing or new `@RestController`
10. **Tests** — add unit tests for domain logic, application service, infrastructure adapter, and controller

## Development Workflow

### Build & Test

All Maven commands must be run from the `backend/` directory.

```bash
# Full build (compiles, tests, coverage check, architecture tests)
cd backend && mvn clean install

# Same as CI
cd backend && mvn -B clean verify

# Run tests only (no install)
cd backend && mvn test

# Test a single module
cd backend && mvn -pl food-quiz-domain test

# Skip tests (use sparingly)
cd backend && mvn clean install -DskipTests
```

### Running Locally

Requires a PostgreSQL instance running on `localhost:5432` with database `postgres`, user `postgres`, password `password` (see `backend/food-quiz-bootstrap/src/main/resources/application.yaml`).

```bash
cd backend/food-quiz-bootstrap
mvn spring-boot:run
```

The server starts on **port 8080**. Available endpoints:
- `GET /quiz/start` — returns a random quiz as JSON
- `GET /actuator/health` — health check
- `GET /actuator/liquibase` — migration status

### Code Coverage Report

After running `mvn verify`, the aggregate HTML report is at:
```
backend/food-quiz-tests-report/target/site/jacoco-aggregate/index.html
```

CI fails if overall coverage or per-file coverage for changed files drops below **80%**.

## Database Management

### Schema Migrations
Liquibase manages all schema changes. Add new changelogs in:
```
backend/food-quiz-bootstrap/src/main/resources/db/changelog/
```
Register each new file in `db.changelog-master.xml`. Never modify `ddl-auto` — it is set to `none`.

### Test Database
Tests use H2 in-memory database via the `test` Spring profile. Test configuration is in:
```
backend/food-quiz-bootstrap/src/test/resources/application-test.yaml
```
Test classes use `@ActiveProfiles("test")`.

## CI/CD

GitHub Actions workflow: `.github/workflows/ci.yml`

- Triggers on push to `main` and pull requests targeting `main`
- JDK: Temurin 25
- Step: `mvn -B clean verify` run from `working-directory: backend` (includes unit tests, integration tests, architecture tests, JaCoCo)
- Coverage gate: 80% overall and 80% on changed files (posted as a PR comment via `jacoco-report` action)
- Additional workflows: dependency review (security), auto-assign, PR labeler, Dependabot

## JPA Entity Inheritance

`QuestionEntity` uses **single-table inheritance** with a `question_type` discriminator column:
- `ChoiceQuestionEntity` — discriminator value `CHOICE`
- `NumberQuestionEntity` — discriminator value `NUMBER`

When adding a new question type, extend `QuestionEntity`, add the discriminator value, and update the Liquibase schema accordingly.
