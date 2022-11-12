package com.mfrancza.jwtrevocationmanager

import com.mfrancza.jwtrevocationmanager.plugins.configureDependencyInjection
import com.mfrancza.jwtrevocationmanager.plugins.configureHTTP
import com.mfrancza.jwtrevocationmanager.plugins.configureRouting
import com.mfrancza.jwtrevocationmanager.plugins.configureSerialization
import com.mfrancza.jwtrevocationmanager.rules.RuleSet
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
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

        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
            val ruleSet = this.body<RuleSet>()
            val secondsSinceTimestamp = Instant.now().epochSecond - ruleSet.timestamp
            assertTrue(ruleSet.rules.isEmpty(), "No rules should be added yet")
            assertTrue( secondsSinceTimestamp >= 0, "Timestamp should be before current time")
            assertTrue( secondsSinceTimestamp < 10, "The timestamp should be recent")
        }
    }
}