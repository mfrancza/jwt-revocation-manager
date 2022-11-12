package com.mfrancza.jwtrevocationmanager

import com.mfrancza.jwtrevocationmanager.plugins.configureDependencyInjection
import com.mfrancza.jwtrevocationmanager.plugins.configureHTTP
import com.mfrancza.jwtrevocationmanager.plugins.configureMonitoring
import com.mfrancza.jwtrevocationmanager.plugins.configureRouting
import com.mfrancza.jwtrevocationmanager.plugins.configureSecurity
import com.mfrancza.jwtrevocationmanager.plugins.configureSerialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureDependencyInjection()
        configureSecurity()
        configureSerialization()
        configureHTTP()
        configureMonitoring()
        configureRouting()
    }.start(wait = true)
}
