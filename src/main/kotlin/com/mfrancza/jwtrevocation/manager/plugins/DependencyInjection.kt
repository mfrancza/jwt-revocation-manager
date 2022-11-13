package com.mfrancza.jwtrevocation.manager.plugins

import com.mfrancza.jwtrevocation.manager.persistence.InMemoryRuleStore
import com.mfrancza.jwtrevocation.manager.persistence.RuleStore
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDependencyInjection() {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<RuleStore> { InMemoryRuleStore() }
            }
        )
    }
}