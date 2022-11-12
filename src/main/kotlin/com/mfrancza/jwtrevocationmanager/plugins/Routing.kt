package com.mfrancza.jwtrevocationmanager.plugins

import com.mfrancza.jwtrevocationmanager.persistence.RuleStore
import com.mfrancza.jwtrevocationmanager.rules.Rule
import com.mfrancza.jwtrevocationmanager.rules.RuleSet
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject
import java.lang.IllegalArgumentException
import java.time.Instant

fun Application.configureRouting() {

    routing {
        route("/rules") {
            val ruleStore by inject<RuleStore>()

            get {
                call.respond(RuleSet(
                    rules = ruleStore.list(),
                    timestamp = Instant.now().epochSecond
                ))
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
