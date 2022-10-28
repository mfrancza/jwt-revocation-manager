package com.mfrancza.jwtrevocationmanager.plugins

import com.mfrancza.jwtrevocationmanager.rules.DateTimeCondition
import com.mfrancza.jwtrevocationmanager.rules.Rule
import com.mfrancza.jwtrevocationmanager.rules.StringCondition
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import java.time.Instant

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/rules") {
            get {
                call.respond(listOf(
                    Rule(
                        ruleId = "0b98fdd9-efcb-41e7-910d-3163c3157bc9",
                        ruleExpires = 1666929810,
                        iss = StringCondition(
                            operation = StringCondition.Operation.Equals,
                            value = "bad.issuer.com"
                        ),
                        iat = DateTimeCondition(
                            operation = DateTimeCondition.Operation.Equals,
                            value = Instant.now().epochSecond
                        )
                    )
                ))
            }
        }
    }
}
