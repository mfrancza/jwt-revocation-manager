package com.mfrancza.jwtrevocation.client

import com.mfrancza.jwtrevocation.rules.RuleSet
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class RuleSetHttpClientTest {

    @Test
    fun retrievedRuleSetIsCached() = runTest {
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

    @Test
    fun retrievedRuleSetMatchesValueFromServer() = runTest {
        val expectedRuleSet = RuleSet(
            rules = listOf(),
            timestamp = Instant.now().epochSecond
        )

        var timesCalled = 0
        val mockEngine = MockEngine { request ->
            timesCalled++
            respond(
                content = ByteReadChannel(Json.encodeToString(expectedRuleSet)),
                status = HttpStatusCode.OK,
                headers = headersOf(
                        Pair(HttpHeaders.ContentType, listOf("application/json")),
                        Pair(HttpHeaders.CacheControl, listOf("max-age=604800"))
                )
            )
        }
        val client = RuleSetHttpClient("https://mfrancza.com/ruleset", mockEngine)

        assertEquals(expectedRuleSet, client.ruleSet(), "The returned rule set should match the one from the server")
        assertEquals(expectedRuleSet, client.ruleSet(), "The returned rule set should still match the one from the server")

        assertEquals(1, timesCalled, "The result should be cached from the first call")
    }
}