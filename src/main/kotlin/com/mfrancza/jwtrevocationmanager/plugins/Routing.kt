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
import kotlin.reflect.jvm.internal.impl.util.ValueParameterCountCheck.Equals

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        route("/rules") {
            get {
                call.respond(listOf(
                    Rule(
                        iss = StringCondition(
                            operation = StringCondition.Operation.Equals,
                            value = "bad.issuer.com"
                        ),
                        iat = DateTimeCondition(
                            operation = DateTimeCondition.Operation.Equals,
                            value = Instant.now().epochSecond
                        )
                    ),

                ))
            }
        }
    }
}
