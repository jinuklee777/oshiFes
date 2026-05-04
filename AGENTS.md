# AGENTS.md

This file provides guidance to Codex when working with code in this repository.
## Commands

```bash
./gradlew build          # Compile and build
./gradlew bootRun        # Run the application locally
./gradlew test           # Run all tests
./gradlew test --tests "com.oshifes.SomeTest"  # Run a single test class
./gradlew clean          # Clean build output
```

## Stack

- **Java 17**, Spring Boot 3.x
- **Spring Data JPA** + **PostgreSQL** for persistence
- **Lombok** for boilerplate reduction (`@Getter`, `@Builder`, `@RequiredArgsConstructor`, etc.)
- **JUnit 5** via `spring-boot-starter-test`

## Architecture

Domain-driven Layered Architecture under `src/main/java/com/oshifes/`:

- **global/** — Global settings (`config/`, `error/`, `common/ApiResponse`)
- **domain/{domain_name}/** — Business logic separated by domain (e.g., event, member)
    - **api/** — REST endpoints (`@RestController`) and DTOs
    - **application/** — Business logic (`@Service`)
    - **dao/** — JPA repositories (`@Repository`, extends `JpaRepository`)
    - **domain/** — JPA entities (`@Entity`)
- **infrastructure/** — External integrations (e.g., 3rd party APIs, external storage)

Database configuration belongs in `src/main/resources/application.properties`. Use `application-{profile}.properties` for environment-specific config (e.g., `application-local.properties` for local DB credentials — do not commit).

## API & Response Guidelines

- **Unified Response**: All API responses MUST be wrapped in a common `ApiResponse<T>` object (containing fields like `success`, `data`, `message`, `errorCode`).
- **Global Error Handling**: Handle exceptions centrally using `@RestControllerAdvice` and define standard `ErrorCode` enums.

## Coding Standards & Patterns

- **Dependency Injection**: Use Constructor Injection via Lombok's `@RequiredArgsConstructor`. Never use `@Autowired` on fields.
- **Entity & DTO**:
    - Never expose Entities directly in API responses.
    - Use Lombok `@Builder` and `@Getter` for DTOs (or Java 17 `record` where appropriate).
    - Use Static Factory Methods (e.g., `from()`, `of()`) inside DTOs to map Entity to DTO.
- **Transactions**: Apply `@Transactional(readOnly = true)` at the Service class level. Explicitly use `@Transactional` on methods that modify data (insert/update/delete).
- **Validation**: Use `@Valid` and Spring Boot validation annotations in Controllers.

## GitHub Workflow

- Issue and PR templates are in Korean (`.github/ISSUE_TEMPLATE/`, `.github/PULL_REQUEST_TEMPLATE.md`)
- PRs should reference related issues

## Agent Workflow

- Before changing code, inspect the existing package structure and follow nearby patterns.
- Prefer small, focused changes over broad refactors unless explicitly requested.
- After code changes, run the narrowest relevant test first, then `./gradlew test` when the change affects shared behavior.
- Do not commit local DB credentials or environment-specific property files.
- If a command fails because of local environment setup, explain the failure and the closest verification performed.

## Automated Issue-to-PR Workflow

This repository uses an AI-assisted workflow:

1. Add `codex-ready` to a GitHub issue to let Codex implement it.
2. Codex opens a PR after running `./gradlew test`.
3. Codex reviews the PR when the `codex-review` label is present.
4. Add `codex-fix` to the PR when Codex review feedback should be applied.
5. Add `claude-final-review` after Codex feedback is handled.

Agent rules for this workflow:

- Always run `./gradlew test` before creating or updating an automated PR.
- Keep PRs focused on the linked issue.
- Do not auto-merge.
- Do not modify local-only configuration or secrets.
- Do not commit production credentials or environment-specific property files.
- When addressing Codex review feedback, only fix valid findings related to the PR diff.
- If review feedback is incorrect or risky, leave the code unchanged and explain why in the PR.
