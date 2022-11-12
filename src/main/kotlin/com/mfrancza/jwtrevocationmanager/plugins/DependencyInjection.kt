package com.mfrancza.jwtrevocationmanager.plugins

import com.mfrancza.jwtrevocationmanager.persistence.InMemoryRuleStore
import com.mfrancza.jwtrevocationmanager.persistence.RuleStore
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