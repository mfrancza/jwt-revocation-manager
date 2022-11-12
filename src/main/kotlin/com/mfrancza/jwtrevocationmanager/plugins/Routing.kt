package com.mfrancza.jwtrevocationmanager.plugins

import com.mfrancza.jwtrevocationmanager.persistence.RuleStore
import com.mfrancza.jwtrevocationmanager.rules.RuleSet
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import java.time.Instant

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/rules") {
            val ruleStore by inject<RuleStore>()
            get {
                call.respond(RuleSet(
                    rules = ruleStore.list(),
                    timestamp = Instant.now().epochSecond
                ))
            }
        }
    }
}
