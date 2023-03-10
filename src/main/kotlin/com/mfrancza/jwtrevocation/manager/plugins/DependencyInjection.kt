package com.mfrancza.jwtrevocation.manager.plugins

import com.mfrancza.jwtrevocation.manager.persistence.InMemoryRuleStore
import com.mfrancza.jwtrevocation.manager.persistence.JDBCRuleStore
import com.mfrancza.jwtrevocation.manager.persistence.RuleStore
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

data class DataStoreSettings(
    val url : String,
    val user: String,
    val password: String
)

fun Application.configureDependencyInjection(dataStoreSettings: DataStoreSettings) {
    install(Koin) {
        slf4jLogger()
        modules(
            module {
                single<RuleStore> {
                    when(dataStoreSettings.url.split(":").first()) {
                        "in-memory" -> InMemoryRuleStore().also { logger.info("Using InMemoryRuleStore") }
                        "jdbc" -> JDBCRuleStore(dataStoreSettings.url, dataStoreSettings.user, dataStoreSettings.password).also{ logger.info("Using JDBCRuleStore")}
                        else -> throw IllegalArgumentException("No supported data store for URL ${dataStoreSettings.url}")
                    }
                }
            }
        )
    }
}