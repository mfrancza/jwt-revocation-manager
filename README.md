# JWT Revocation Manager

## What

The JWT Revocation Manager is a webservice for manging rules for revoking JWT tokens.  The rules managed by the service are distributed to application servers which use JWTs for authorization, which should not accept tokens which meet one of the rules.

## Why

The JWT token format is a popular solution for authorization since it does not require the management of server-side state, but because there is no server side state associated with a token, the claims in a token cannot be altered or revoked directly.   This can be mitigated to a degree by the use of short-lived tokens with exp claim values in the near future.  Using the exp claim limits the staleness of the other claims in the token, but requires more frequent authorization transactions.

Support for revoking specific tokens is provided in some IAM products, but this approach requires tracking the jti values of issued tokens for inclusion.  This solution scales poorly when a large number of tokens need to be revoked and requires knowledge of the specific tokens to revoke, which might not always be easily obtainable if the history of token issuance isn’t in a queryable data store.

The JWT Revocation Manager instead uses rules to express which tokens should not be accepted, so knowing the specific jts is not required and large scale revocation of tokens with common characteristics requires less resources.

## How

## Running the Application

The application supports the packaging and deployment options described at https://ktor.io/docs/deploy.html.

* run - runs the server locally
* buildFatJar - builds a fat JAR with the application's dependencies
* buildImage - builds a docker image for running the application

### Environment Variables

The server is configured using the below environment variables

#### Security

* JRM_SECURITY_ISSUER - the expected issuer for JWT tokens; if it does not match, the API will return that the call is unauthorized
* JRM_SECURITY_AUDIENCE - the expected audience for JWT tokens; if it does not match, the API will return that the call is unauthorized

#### Data Store

* JRM_DATA_STORE_URL - The URL of the data store to use; currently supports "in-memory" and JDBC connection strings; only drivers for postgres are automatically included in the class path
* JRM_DATA_STORE_USER - the user to authenticate to the datastore as
* JRM_DATA_STORE_PASSWORD - the password for the user

## Initializing the Application

To initialize the data store, set JRM_INITIALIZE when running the application.  Only a single instance should be started this way.

## Calling the Management API

Currently, there is no official client library or user interface, but a postman collection documenting the API is included in the postman directory.

## Using RuleSets to Revoke JWTs

The /ruleset endpoint is intended to be called by clients using the rules to check for revoked tokens.

The jwt-revocation-ruleset package provides a multiplatform Kotlin client library that provides authorization and caching.

The jwt-revocation-ktor-server-auth package provides convenience functions for validating JWTCredentials from io.ktor:ktor-server-auth-jwt-jvm.

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