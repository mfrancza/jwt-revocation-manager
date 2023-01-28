# JWT Revocation Manager

## What

The JWT Revocation Manager is a webservice for manging rules for revoking JWT tokens.  The rules managed by the service are distributed to application servers which use JWTs for authorization, which should not accept tokens which meet one of the rules.

## Why

The JWT token format is a popular solution for authorization since it does not require the management of server-side state, but because there is no server side state associated with a token, the claims in a token cannot be altered or revoked directly.   This can be mitigated to a degree by the use of short-lived tokens with exp claim values in the near future.  Using the exp claim limits the staleness of the other claims in the token, but requires more frequent authorization transactions.

Support for revoking specific tokens is provided in some IAM products, but this approach requires tracking the jti values of issued tokens for inclusion.  This solution scales poorly when a large number of tokens need to be revoked and requires knowledge of the specific tokens to revoke, which might not always be easily obtainable if the history of token issuance isn’t in a queryable data store.

The JWT Revocation Manager instead uses rules to express which tokens should not be accepted, so knowing the specific jts is not required and large scale revocation of tokens with common characteristics requires less resources.

## How

## Running the Server

In the current, pre-release state, ./gradlew run is the recommended way to launch the server.

## When

### MVP Phase - In Progress

Phase 1's scope is the minimum viable application - defining the minimum set of components to make the application useful.  It will be delivered together.

Scope
* API Endpoints
* Client Authorization
* JDBC Data Stores
* Instructions on how to manage API by scripts
* Kotlin Ruleset Client and Ktor server component
* Containerization for Server
* Integration Tests

### Upcoming

* Web interface
* Metrics
* Management Client Library
* IaC Templates
* Load Testing
* RuleSet Scalability
* Incremental 