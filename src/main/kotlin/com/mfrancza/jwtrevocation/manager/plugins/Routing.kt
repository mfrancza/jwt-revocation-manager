package com.mfrancza.jwtrevocation.manager.plugins

import com.mfrancza.jwtrevocation.manager.persistence.RuleStore
import com.mfrancza.jwtrevocation.rules.Rule
import com.mfrancza.jwtrevocation.rules.RuleSet
import io.ktor.http.CacheControl
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.cachingheaders.caching
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import java.lang.IllegalArgumentException
import java.time.Instant

fun Application.configureRouting() {

    routing {

        val ruleStore by inject<RuleStore>()

        authenticate("auth-jwt") {
            route("/ruleset") {
                get {
                    val ruleSet = RuleSet(
                        rules = ruleStore.list(),
                        timestamp = Instant.now().epochSecond
                    )
                    call.caching = CachingOptions(CacheControl.MaxAge(5))
                    call.respond(ruleSet)
                }
            }
            route("/rules") {
                get {
                    call.respond(
                        ruleStore.list()
                    )
                }
                post {
                    val newRule = call.receive<Rule>()
                    if (newRule.ruleId != null) {
                        throw IllegalArgumentException()
                    }
                    call.respond(ruleStore.create(newRule))
                }
                route("/{ruleId}") {
                    get {
                        val ruleId = call.parameters["ruleId"] ?: throw IllegalArgumentException()
                        val rule = ruleStore.read(ruleId) ?: call.respond(HttpStatusCode.NotFound, "Rule not found")
                        call.respond(rule)
                    }
                    delete {
                        val ruleId = call.parameters["ruleId"] ?: throw IllegalArgumentException()
                        val rule = ruleStore.delete(ruleId) ?: call.respond(HttpStatusCode.NotFound, "Rule not found")
                        call.respond(rule)
                    }
                }
            }
        }

    }
}
