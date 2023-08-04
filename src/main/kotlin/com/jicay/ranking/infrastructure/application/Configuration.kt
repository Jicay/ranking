package com.jicay.ranking.infrastructure.application

import com.jicay.ranking.infrastructure.application.plugins.configureRouting
import com.jicay.ranking.infrastructure.application.plugins.configureSerialization
import com.jicay.ranking.infrastructure.secondary.dao.config.migrateDatabaseSchema
import com.jicay.ranking.infrastructure.secondary.dao.settings.MongoSettings
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(appModule, mongoSettings(environment.config))
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    val mongoSettings by inject<MongoSettings>()
    migrateDatabaseSchema(mongoSettings)

    configureRouting()
    configureSerialization()
}
