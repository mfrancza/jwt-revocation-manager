package com.mfrancza.jwtrevocation.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mfrancza.jwtrevocation.manager.plugins.SecuritySettings
import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.RuleSet
import com.mfrancza.jwtrevocation.rules.conditions.StringEquals
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRuleManagement() = testApplication {
        val issuer = "testIssuer"
        val audience = "testAudience"
        val jwtSecret = "testSecret"

        val ruleRefreshFrequencySeconds = 10

        application(makeJwtRevocationManager(
            SecuritySettings(
                audience,
                issuer,
                SecuritySettings.HS256(jwtSecret)
            )
        ))

        //get a token with access to all the methods
        val token = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("scope", "GET:/ruleset GET:/rules POST:/rules GET:/rules/{ruleId} DELETE:/rules/{ruleId}")
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtSecret))

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(token, "NotUsed")
                    }
                }
            }
        }

        //add a couple rules
        val expectedRules = mutableListOf<Rule>()

        //check initial contents of rule store
        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
            val rules = this.body<List<Rule>>()
            validateExpectedRules(expectedRules, rules)
        }
        client.get("/ruleset").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)
        }


        //create a couple rules
        val newIssRule = Rule(
            ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
            iss = listOf(
                StringEquals(
                    value = "bad-iss.mfrancza.com"
                )
            )
        )
        client.post("/rules") {
            contentType(ContentType.Application.Json)
            setBody(newIssRule)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val createdRule = call.body<Rule>()
            expectedRules.add(createdRule)
            assertNotNull(createdRule.ruleId)
            assertEquals(createdRule.copy(ruleId = null), newIssRule)
        }

        val newAudRule = Rule(
            ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
            aud = listOf(
                StringEquals(
                    value = "bad-aud.mfrancza.com"
                )
            )
        )
        client.post("/rules") {
            contentType(ContentType.Application.Json)
            setBody(newAudRule)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val createdRule = call.body<Rule>()
            expectedRules.add(createdRule)
            assertNotNull(createdRule.ruleId)
            assertEquals(createdRule.copy(ruleId = null), newAudRule)
        }

        //check that the retrieved rules match the created ones
        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
            val rules = this.body<List<Rule>>()
            validateExpectedRules(expectedRules, rules)
        }
        client.get("/ruleset").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)

            ruleSet.rules.forEach {
                client.get("/rules/${it.ruleId}").apply {
                    assertEquals(it, this.body(), "The rule returned individually should match the one in the rule set")
                }
            }
        }

        //delete one of the rules
        val ruleToDelete = expectedRules.removeFirst()
        client.delete("/rules/${ruleToDelete.ruleId}").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals(ruleToDelete, this.body(), "The deleted rule should match the one whose ID was passed")
        }

        //verify it is no longer included in the results
        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
            val rules = this.body<List<Rule>>()
            validateExpectedRules(expectedRules, rules)
        }
        client.get("/ruleset").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)
        }

        //get a token with access to only GET methods
        val readOnlyToken = JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("scope", "GET:/ruleset GET:/rules GET:/rules/{ruleId}")
            .withExpiresAt(Date(System.currentTimeMillis() + 60000))
            .sign(Algorithm.HMAC256(jwtSecret))

        val readOnlyClient = createClient {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(readOnlyToken, "NotUsed")
                    }
                }
            }
        }

        //get the ruleset
        readOnlyClient.get("/ruleset").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)
        }

        //try to delete a rule, which should result in a 403 since the client only has read permissions
        readOnlyClient.delete("/rules/${expectedRules.first().ruleId}").apply {
            assertEquals(HttpStatusCode.Forbidden, status)
        }

        //create an unauthenticated client
        val unauthenticatedClient = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        //getting the ruleset should return a 401 since the client is unauthenticated
        unauthenticatedClient.get("/ruleset").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

        //create a rule revoking tokens for the client's issuer
        val ownIssRule = Rule(
            ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
            iss = listOf(
                StringEquals(
                    value = issuer
                )
            )
        )
        client.post("/rules") {
            contentType(ContentType.Application.Json)
            setBody(ownIssRule)
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val createdRule = call.body<Rule>()
            expectedRules.add(createdRule)
            assertNotNull(createdRule.ruleId)
            assertEquals(createdRule.copy(ruleId = null), ownIssRule)
        }

        //getting the ruleset should return a 401 since the client's token is revoked by the rule
        client.get("/ruleset").apply {
            assertEquals(HttpStatusCode.Unauthorized, status)
        }

    }

    private fun validateExpectedRules(expectedRules: List<Rule>, actualRules: List<Rule>) {
        assertEquals(expectedRules.size, actualRules.size, "The number of rules should be the same")
        expectedRules.forEach {
            assertContains(actualRules, it, "The expected rule should be contained in the rules")
        }
    }

    private fun validateTimestamp(timestamp: Long, withinSeconds: Int) {
        val secondsSinceTimestamp = Instant.now().epochSecond - timestamp
        assertTrue(secondsSinceTimestamp >= 0, "Timestamp should be before current time")
        assertTrue(secondsSinceTimestamp < withinSeconds, "The timestamp should be recent")
    }
}