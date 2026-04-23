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

REST API — service account auth (CamundaApiController)
    → autowired CamundaClient (service account credentials from application.properties)
    → GET /api/camunda/userTasks  (assignee extracted from JWT)

REST API — user token passthrough (CamundaUserApiController)
    → short-lived CamundaClient built per request with caller's JWT
    → GET /userApi/camunda/userTasks  (Camunda API called in user's own context)

Spring Security (SecurityConfig)
    → validates Bearer JWT against Keycloak JWKS
    → protects /api/** routes

React frontend (frontend/)
    → Keycloak PKCE login → Bearer token → calls backend API
    → displays tasks assigned to logged-in user

Zeebe Job Workers (MyWorker)
    → MyService (business logic)
    → ProcessVariables (data model)

BPMN models (src/main/resources/models/)
    → auto-deployed by ProcessApplication at startup via @Deployment
```

**Key classes:**
- `ProcessApplication` — entry point; `@Deployment` auto-deploys all `classpath*:/models/*.*`
- `ProcessController` — `POST /process/start` and `POST /process/message/{messageName}/{correlationKey}`
- `CamundaApiController` — `GET /api/camunda/userTasks`; uses autowired service-account `CamundaClient`; extracts assignee from JWT `preferred_username` claim
- `CamundaUserApiController` — `GET /userApi/camunda/userTasks`; builds a short-lived `CamundaClient` per request using the caller's JWT as a pass-through `CredentialsProvider`; Camunda API calls run in the user's own authorization context
- `SecurityConfig` — Spring Security OAuth2 resource server; validates JWTs against Keycloak; CORS configured for `localhost:3000`
- `MyWorker` — two patterns: `@JobWorker` returning variables (simple), `@JobWorker` with `JobClient` injection (manual completion/error/retry handling)
- `MyService` — replace `myOperation()` with real business logic
- `ProcessVariables` — Jackson DTO with `@JsonInclude(NON_NULL)`; add fields here for new process variables
- `ProcessConstants` — holds `BPMN_PROCESS_ID`; update when renaming the process

## Frontend

React app in `frontend/` using Vite + `keycloak-js`. Proxies `/api` to the Spring Boot backend at `localhost:8080`.

```bash
cd frontend
npm install        # first time only
npm run dev        # starts at http://localhost:3000
```

Requires a **public** Keycloak client named `camunda-react-app` with:
- Standard flow enabled, client authentication OFF
- Valid redirect URIs: `http://localhost:3000/*`
- Web origins: `http://localhost:3000`

Update `frontend/src/keycloak.js` if the Keycloak URL or realm name differs from `application.properties`.

## Keycloak / Auth

All `/api/**` endpoints require a Bearer JWT. The backend validates tokens using:
```
spring.security.oauth2.resourceserver.jwt.issuer-uri=<keycloak-realm-url>
```

The service-account client (`camunda.client.auth.client-id`) used by the app to connect to Camunda needs these Keycloak roles assigned to its service account:
- `orchestration-api read:*`
- `orchestration-api write:*`

## Configuration

`application.properties` is the active config. Pre-built variants live in `src/main/resources/configs/` — copy the relevant one to `src/main/resources/` to switch environments:

| File | Environment |
|------|-------------|
| `application.local.properties` | Local Docker Compose |
| `application.saas.properties` | Camunda SaaS |
| `application.keycloak.properties` | Self-managed with Keycloak |
| `application.8.7.aws.properties` / `application.8.7.gcp.properties` | Cloud-hosted self-managed |

## Tech Stack

- Java 21, Spring Boot 4.0.3
- Camunda Spring SDK 8.9.0 (`io.camunda:camunda-spring-boot-starter`)
- Spring Security + OAuth2 Resource Server (JWT validation against Keycloak)
- React 18 + Vite + keycloak-js (frontend)
- Spotless (Google Java Format) enforced at build time
