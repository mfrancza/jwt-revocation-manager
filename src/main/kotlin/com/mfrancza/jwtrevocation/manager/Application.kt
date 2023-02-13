package com.mfrancza.jwtrevocation.manager

import com.mfrancza.jwtrevocation.manager.plugins.SecuritySettings
import com.mfrancza.jwtrevocation.manager.plugins.configureDependencyInjection
import com.mfrancza.jwtrevocation.manager.plugins.configureHTTP
import com.mfrancza.jwtrevocation.manager.plugins.configureMonitoring
import com.mfrancza.jwtrevocation.manager.plugins.configureRouting
import com.mfrancza.jwtrevocation.manager.plugins.configureSecurity
import com.mfrancza.jwtrevocation.manager.plugins.configureSerialization
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty



fun makeJwtRevocationManager(securitySettings: SecuritySettings) : Application.() -> Unit {
    return {
        configureDependencyInjection()
        configureSecurity(securitySettings)
        configureSerialization()
        configureHTTP()
        configureMonitoring()
        configureRouting()
    }
}

fun main() {
    val securitySettings = SecuritySettings(
        issuer = System.getenv("JRM_SECURITY_ISSUER"),
        audience = System.getenv("JRM_SECURITY_AUDIENCE"),
        verification = SecuritySettings.RS256
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = makeJwtRevocationManager(
        securitySettings
    )) .start(wait = true)
}

