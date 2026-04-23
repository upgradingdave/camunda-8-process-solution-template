# AGENTS.md

This file provides guidance to AI coding agents when working with code in this repository.

## Project Overview

Spring Boot template for building process automation solutions on Camunda Platform 8 (Zeebe workflow engine). The application deploys BPMN models at startup and exposes REST endpoints to start process instances and publish messages.

## Commands

```bash
# Build
./mvnw clean install           # Full build with tests
./mvnw clean compile           # Compile only
./mvnw spring-boot:run         # Run application

# Docker image (Spring Boot Buildpacks)
./mvnw -DskipTests spring-boot:build-image

# Code formatting (Google Java Format via Spotless)
./mvnw spotless:check          # Check formatting
./mvnw spotless:apply          # Fix formatting

# Local Camunda Platform 8 infrastructure
docker compose up -d                              # Full stack (Zeebe, Operate, Tasklist, Identity, Keycloak, Elasticsearch)
docker compose -f docker-compose-core.yaml up -d  # Minimal: Zeebe, Operate, Tasklist only
docker compose down -v                            # Tear down
```

Local Operate UI: `http://localhost:8081`, Tasklist: `http://localhost:8082`, credentials: `demo`/`demo`

## Architecture

```
REST API (ProcessController)
    → ProcessConstants (process ID)
    → CamundaClient (gRPC/REST to Zeebe)

Zeebe Job Workers (MyWorker)
    → MyService (business logic)
    → ProcessVariables (data model)

BPMN models (src/main/resources/models/)
    → auto-deployed by ProcessApplication at startup via @Deployment
```

**Key classes:**
- `ProcessApplication` — entry point; `@Deployment` auto-deploys all `classpath*:/models/*.*`
- `ProcessController` — `POST /process/start` and `POST /process/message/{messageName}/{correlationKey}`
- `MyWorker` — two patterns: `@JobWorker` returning variables (simple), `@JobWorker` with `JobClient` injection (manual completion/error/retry handling)
- `MyService` — replace `myOperation()` with real business logic
- `ProcessVariables` — Jackson DTO with `@JsonInclude(NON_NULL)`; add fields here for new process variables
- `ProcessConstants` — holds `BPMN_PROCESS_ID`; update when renaming the process

## Configuration

`application.properties` is the active config. Pre-built variants live in `src/main/resources/configs/` — copy the relevant one to `src/main/resources/` to switch environments:

| File | Environment |
|------|-------------|
| `application.local.properties` | Local Docker Compose |
| `application.saas.properties` | Camunda SaaS |
| `application.keycloak.properties` | Self-managed with Keycloak |
| `application.8.7.aws.properties` / `application.8.7.gcp.properties` | Cloud-hosted self-managed |

The active config points to a remote self-managed instance (`dave01.gke.c8sm.com`) with OIDC auth. For local development, swap in `application.local.properties`.

## Tech Stack

- Java 21, Spring Boot 4.0.3
- Camunda Spring SDK 8.9.0-alpha5 (`io.camunda:spring-boot-starter-camunda-sdk`)
- Spotless (Google Java Format) enforced at build time
