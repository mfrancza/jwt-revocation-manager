package com.mfrancza.jwtrevocation.client

import com.mfrancza.jwtrevocation.rules.RuleSet
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class RuleSetHttpClientTest {

    @Test
    fun retrievedRuleSetMatchesValueFromServer() = runTest {
        val expectedRuleSet = RuleSet(
            rules = listOf(),
            timestamp = Instant.now().epochSecond
        )
        val mockEngine = MockEngine { request ->
            respond(
                content = ByteReadChannel(Json.encodeToString(expectedRuleSet)),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = RuleSetHttpClient("https://mfrancza.com/ruleset", mockEngine)

        assertEquals(expectedRuleSet, client.ruleSet(), "The returned rule set should match the one from the server")
    }
}