package com.mfrancza.jwtrevocation.manager

import com.mfrancza.jwtrevocation.manager.plugins.configureDependencyInjection
import com.mfrancza.jwtrevocation.manager.plugins.configureHTTP
import com.mfrancza.jwtrevocation.manager.plugins.configureRouting
import com.mfrancza.jwtrevocation.manager.plugins.configureSerialization
import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.RuleSet
import com.mfrancza.jwtrevocation.rules.StringCondition
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
import io.ktor.client.call.body
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
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testRuleManagement() = testApplication {

        val ruleRefreshFrequencySeconds = 10

        application {
            configureDependencyInjection()
            configureSerialization()
            configureHTTP()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        //add a couple rules
        val expectedRules = mutableListOf<Rule>()

        //check initial contents of rule store
        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)
        }

        val newIssRule = Rule(
            ruleExpires = Instant.now().plus(1, ChronoUnit.DAYS).epochSecond,
            iss = listOf(
                StringCondition(
                    operation = StringCondition.Operation.Equals,
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
                StringCondition(
                    operation = StringCondition.Operation.Equals,
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
            val ruleSet = this.body<RuleSet>()
            validateTimestamp(ruleSet.timestamp, ruleRefreshFrequencySeconds)
            validateExpectedRules(expectedRules, ruleSet.rules)
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