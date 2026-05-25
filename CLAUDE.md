# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Commands

Built with Gradle (Kotlin DSL) on a JDK 21 toolchain. Use the wrapper:

- `./gradlew run` — run the server locally on port 8080
- `./gradlew build` — compile, run tests, assemble
- `./gradlew test` — run all tests
- `./gradlew test --tests "com.mfrancza.jwtrevocation.manager.ApplicationTest.testRuleManagement"` — run a single test
- `./gradlew buildFatJar` — fat JAR with dependencies (Ktor plugin)
- `./gradlew buildImage` — build a Docker image (Ktor plugin)
- `./gradlew publish` — publish to GitHub Packages (release workflow does this on `release: created`)

### Required environment for builds and publishes

Dependencies `jwt-revocation-rules-jvm` and `jwt-revocation-ktor-server-auth` are pulled from GitHub Packages, so even local builds need:

- `GITHUB_ACTOR` — GitHub username
- `GITHUB_TOKEN` — PAT with `read:packages` (and `write:packages` for `publish`)

Without these, dependency resolution will fail.

### Runtime environment variables

- `JRM_SECURITY_ISSUER`, `JRM_SECURITY_AUDIENCE` — JWT validation; `main()` requires both
- `JRM_DATA_STORE_URL` — `in-memory` (default) or a JDBC URL; only Postgres driver is bundled (H2 is test-only)
- `JRM_DATA_STORE_USER`, `JRM_DATA_STORE_PASSWORD` — datastore credentials
- `JRM_INITIALIZE=true` — runs `RuleStore.initialize()` (creates JDBC schema) then continues to start the server. Should be set on exactly one instance when bootstrapping a new datastore.

`main()` hardcodes `SecuritySettings.RS256` (JWKs fetched from the issuer URL). Tests construct the application via `makeJwtRevocationManager(...)` directly and inject `HS256` with a shared secret — bypass `main()` rather than mocking it.

## Architecture

Ktor (Netty) server using Koin for DI. `Application.kt::makeJwtRevocationManager` returns the module function; plugins are configured in fixed order in `plugins/`:

1. `DependencyInjection` — binds a single `RuleStore` based on `DataStoreSettings.url` prefix (`in-memory` → `InMemoryRuleStore`, `jdbc` → `JDBCRuleStore`). `makeRuleStore` is the dispatch point — add new store types here.
2. `Security` — installs the `auth-jwt` provider. The JWT validator calls `notRevoked(ruleStore.ruleSet())` from `jwt-revocation-ktor-server-auth`, so **the manager's own API enforces its own revocation rules against incoming admin tokens**. A rule that matches the admin's token will lock them out.
3. `Serialization`, `HTTP`, `Monitoring`, `Routing`.

### Authorization model

All routes are inside `authenticate("auth-jwt") { ... }`. Each handler calls `validateScope("METHOD:/path") { ... }`, which checks the JWT's `scope` claim (space-delimited) for a literal `METHOD:/path` string — including the literal `{ruleId}` placeholder, e.g. `GET:/rules/{ruleId}`, not the resolved id. When adding a route, add a `validateScope` call with the exact `METHOD:/literal-path` and update token-issuing docs/tests accordingly.

Note: `validateScope` calls `call.respond(Forbidden)` on failure but does **not** return — the `block` runs regardless. Treat this as a known shape when editing; if you change it, audit all callers.

### Routes (`plugins/Routing.kt`)

- `GET /ruleset` — full `RuleSet` for clients to cache; 5-second `Cache-Control: max-age`
- `POST /revoked` — server-side check: accepts `Claims`, returns whether any rule matches; 5-second cache
- `GET/POST /rules`, `GET/DELETE /rules/{ruleId}` — admin CRUD. No update; mutate by delete+create. `POST` rejects rules with a pre-set `ruleId`.
- `GET /metrics-micrometer` — Prometheus scrape endpoint, also scope-gated (`GET:/metrics-micrometer`). Defined in `plugins/Monitoring.kt`, not `Routing.kt`, so it uses an inline `hasScope` check rather than the local `validateScope` helper.

### Rule store

`RuleStore` interface (create, read, delete, list with cursor, initialize, ruleSet). Two implementations:

- `InMemoryRuleStore` — `HashMap`; cursor is a stringified offset into `values.toList()` (unstable across insertions — fine for tests, not production).
- `JDBCRuleStore` — Exposed (`exposed-core` + `exposed-jdbc` 1.3.0; symbols live under `org.jetbrains.exposed.v1.core.*` and `org.jetbrains.exposed.v1.jdbc.*`). Single `Rules` table; JWT-claim conditions (`iss`, `sub`, `aud`, `exp`, `nbf`, `iat`, `jti`) are stored as JSON-serialized `kotlinx.serialization` blobs in `text` columns — not normalized, because the app never queries inside them. Cursor is a stringified offset; `list()` uses SQL `LIMIT/OFFSET` when `limit` is set, otherwise reads all and slices.

Domain types (`Rule`, `RuleSet`, `Claims`, `conditions.*`) come from the external `jwt-revocation-rules` library — do not redefine them here.

### Tests

- `ApplicationTest` — full Ktor `testApplication` with `HS256` and in-memory store; mints its own tokens.
- `RuleStoreTest` — abstract base; `InMemoryRuleStoreTest` and `JDBCRuleStoreTest` (H2 in-memory: `jdbc:h2:mem:test;DB_CLOSE_DELAY=-1`) extend it. New stores should extend `RuleStoreTest`.
- `SecurityTest` — JWT/scope behavior.

## API reference

A Postman collection in `postman/` documents the management API; there is no generated OpenAPI spec.
