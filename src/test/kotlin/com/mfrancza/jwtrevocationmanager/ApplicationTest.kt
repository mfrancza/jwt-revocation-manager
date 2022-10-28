package com.mfrancza.jwtrevocationmanager

import com.mfrancza.jwtrevocationmanager.plugins.configureHTTP
import com.mfrancza.jwtrevocationmanager.plugins.configureRouting
import com.mfrancza.jwtrevocationmanager.plugins.configureSerialization
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureSerialization()
            configureHTTP()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
        client.get("/rules").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }
}