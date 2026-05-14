# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## Project Overview

**FinTrack** is a personal finance tracking REST API that allows users to manage transactions, budgets, and accounts.

- **Main purpose:** Backend service exposing a secured REST API for financial data management
- **Status:** Greenfield — scaffold is in place, business logic is yet to be implemented

### Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL |
| Build | Maven (via wrapper `./mvnw`) |
| Utilities | Lombok, Spring DevTools |

---

## Technology Stack

### Frameworks & Libraries
- **Spring Boot 3.5** — auto-configuration, embedded Tomcat, production-ready defaults
- **Spring Security** — all endpoints secured by default; a `SecurityFilterChain` bean must be defined to open/configure access
- **Spring Data JPA** — repository interfaces over Hibernate ORM
- **Hibernate** — DDL managed via `ddl-auto: update` (schema evolves from entity definitions)
- **Lombok** — compile-time annotation processor; excluded from the final JAR
- **Spring DevTools** — hot reload during local development

### Build Tools
- **Maven Wrapper (`./mvnw`)** — use this instead of a system-installed Maven to ensure consistent versions
- Lombok is wired as an annotation processor in both `compile` and `testCompile` Maven executions

---

## Architecture Patterns

### Design Patterns
- **Layered architecture** — strict separation between web, service, and data layers
- **Repository pattern** — data access exclusively through Spring Data JPA interfaces
- **DTO pattern** — (to be implemented) keep JPA entities internal; expose DTOs from controllers

### Folder Structure

```
src/main/java/com/daniela/AI_Onboarding___FinTrack/
├── AiOnboardingFinTrackApplication.java   ← entry point
├── controller/     ← REST controllers (@RestController)
├── service/        ← business logic (@Service)
├── repository/     ← JPA interfaces (extends JpaRepository)
├── model/          ← JPA entities (@Entity)
├── dto/            ← request/response objects
└── config/         ← Spring Security and other configuration beans
```

### Module Organization
- Each domain concept (e.g., `transaction`, `account`, `budget`) should have its own controller, service, repository, and entity
- Security configuration lives in `config/SecurityConfig.java` as a `@Configuration` class defining a `SecurityFilterChain` bean

---

## Domain Model

Full diagram and schema: [`domain-model.md`](domain-model.md)

### Entity Relationships
- **USER (1:N) ACCOUNT** — one user owns multiple accounts
- **ACCOUNT (1:N) TRANSACTION** — one account has many transactions
- **TRANSACTION (N:1) CATEGORY** — many transactions share one category
- **USER (1:N) BUDGET** — one user sets multiple budgets
- **BUDGET (N:1) CATEGORY** — a budget targets one category

### Key Entities

| Entity | PK | Notable Fields |
|---|---|---|
| `users` | UUID | `email` (UNIQUE), `password_hash` |
| `accounts` | UUID | `user_id` (FK), `type` (account_type enum), `currency` VARCHAR(3), `initial_balance` |
| `transactions` | UUID | `account_id` (FK), `category_id` (FK), `amount`, `transaction_date` TIMESTAMPTZ |
| `categories` | UUID | `name` (UNIQUE), `type` (category_type enum), `deleted_at` (soft delete) |
| `budgets` | UUID | `user_id` (FK), `category_id` (FK), `amount`, `start_date`, `end_date` |

### Design Notes
- All PKs are **UUID**; all timestamps are **TIMESTAMPTZ**
- Soft delete applies only to `categories` via `deleted_at` (nullable); other tables use hard delete
- `account_type` enum: `SAVINGS`, `CHECKING`, `CREDIT_CARD`
- `category_type` enum: `INCOME`, `EXPENSE`
- `transactions` has no `type` column — transaction type is derived from the linked `category.type`
- `budgets` uses a `start_date`/`end_date` date range instead of a period enum

---

## Code Style Guide

This project follows the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) with the Spring Boot conventions below layered on top.

### Naming Conventions
- Classes: `UpperCamelCase` — e.g., `TransactionService`, `AccountController`
- Methods, fields, variables, parameters: `lowerCamelCase`
- Constants (`static final`): `UPPER_SNAKE_CASE`
- Packages: `lowercase`, no underscores
- REST endpoints: `kebab-case` — e.g., `/api/financial-accounts`
- Database columns: `snake_case` (mapped via `@Column(name = "...")` or Hibernate naming strategy)
- Type parameters: single letter or `ClassNameT` — e.g., `T`, `RequestT`

### Formatting
- **Indentation:** 2 spaces per block level (Google style) — not 4
- **Line length:** 100 characters maximum
- **Braces:** K&R style — opening brace on the same line, closing on a new line; always use braces even for single-line `if`/`for`/`while` bodies
- **Imports:** no wildcard imports; static imports grouped separately above non-static imports
- One statement per line; one top-level class per file

### Comment & Javadoc Style
- Only comment non-obvious logic — avoid restating what the code already says
- Javadoc required on all `public` classes and methods; skip on internal service/repository implementations
- Javadoc summary: short noun/verb phrase (not a full sentence); tags in order: `@param`, `@return`, `@throws`
- Lombok removes the need for manual getter/setter/constructor comments

### Java Practices (from Oracle & Google)
- Always annotate overriding methods with `@Override`
- Never silently swallow exceptions — log or rethrow with context
- Reference static members via class name, not via an instance
- Prefer constructor injection over field injection (`@Autowired` on fields is discouraged)
- Use `_` for unnamed variables/patterns where Java 21 syntax allows

### Spring Boot REST Conventions (from Spring guides)
- Use specific mapping annotations (`@GetMapping`, `@PostMapping`, etc.) — not `@RequestMapping` on methods
- Return `ResponseEntity<T>` when you need to control the HTTP status explicitly:
  - `201 Created` + `Location` header for POST
  - `204 No Content` for DELETE
  - `200 OK` for GET/PUT
- Centralize exception handling in a `@RestControllerAdvice` class; map domain exceptions to HTTP status codes with `@ExceptionHandler`
- Never expose JPA entities directly from controllers — always map to/from DTOs
- Keep controllers thin: delegate all business logic to the service layer

---

## Development Conventions

### Git Workflow
- Branch from `main` for each feature or fix
- Use descriptive branch names: `feature/add-transaction-entity`, `fix/security-config`
- Commit messages should be imperative and concise: `Add Transaction entity with JPA mappings`

### Testing Requirements
- Test class naming: `<ClassName>Tests.java`
- Use `@SpringBootTest` for integration tests that need the full Spring context
- Use `@WebMvcTest` for controller-layer tests (faster, no full context)
- Use `spring-security-test` (`@WithMockUser`, `SecurityMockMvcRequestPostProcessors`) when testing secured endpoints

### Documentation Style
- Update `CLAUDE.md` when adding new architectural patterns, packages, or non-obvious conventions
- Keep `application.yaml` commented when adding new configuration blocks

---

## Commands

```bash
# Run the application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=TransactionServiceTests

# Build JAR
./mvnw clean package

# Build without running tests
./mvnw clean package -DskipTests
```

### Key Configuration

- **Database:** PostgreSQL at `localhost:5432/mydb` — credentials in `src/main/resources/application.yaml`
- **DDL:** `hibernate.ddl-auto=update` — schema is auto-updated from entity changes; never use `create-drop` in production
- **SQL logging:** `show-sql: true` is enabled — useful for dev, should be disabled before production

---

## AI Assistant Instructions

### How Claude Should Help
- Implement features following the layered architecture described above — always create controller + service + repository + entity together for a new domain
- Prefer Spring idioms over manual boilerplate (use `JpaRepository` methods, `@Transactional`, Spring Security abstractions)
- Use Lombok annotations to keep classes concise
- When adding security rules, define them inside a `SecurityFilterChain` bean in `config/SecurityConfig.java`

### What to Avoid
- Do not use field injection (`@Autowired` on fields) — use constructor injection
- Do not expose JPA entities directly from controllers — use DTOs
- Do not hardcode credentials or secrets — use `application.yaml` or environment variables
- Do not use `ddl-auto: create` or `create-drop` — it will wipe data

### Preferences for Solutions
- Prefer simple, readable solutions over clever ones
- When multiple approaches exist, briefly explain the trade-off before implementing
- If a task requires changes across multiple layers (entity → repo → service → controller), implement all layers in one go rather than leaving stubs
