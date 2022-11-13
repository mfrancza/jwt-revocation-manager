package com.mfrancza.jwtrevocation.manager

import com.mfrancza.jwtrevocation.manager.plugins.configureDependencyInjection
import com.mfrancza.jwtrevocation.manager.plugins.configureHTTP
import com.mfrancza.jwtrevocation.manager.plugins.configureMonitoring
import com.mfrancza.jwtrevocation.manager.plugins.configureRouting
import com.mfrancza.jwtrevocation.manager.plugins.configureSecurity
import com.mfrancza.jwtrevocation.manager.plugins.configureSerialization
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
