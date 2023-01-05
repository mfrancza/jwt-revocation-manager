package com.mfrancza.jwtrevocation.client

import com.mfrancza.jwtrevocation.rules.RuleSet
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.request.get
import io.ktor.http.Url
import io.ktor.http.takeFrom
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

/**
 * Retrieves a ruleset from an HTTP endpoint which provides it as JSON
 */
class RuleSetHttpClient(private val ruleServerUrl: String, engine: HttpClientEngine) {

    private val httpClient = HttpClient(engine) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCache)
    }

    suspend fun ruleSet() : RuleSet = httpClient.get { url {takeFrom(ruleServerUrl)} }.body()
}