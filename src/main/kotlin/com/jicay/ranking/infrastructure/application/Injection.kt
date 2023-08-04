package com.jicay.ranking.infrastructure.application

import com.jicay.ranking.domain.PlayerManagement
import com.jicay.ranking.domain.port.PlayerPort
import com.jicay.ranking.infrastructure.secondary.adapter.PlayerDao
import com.jicay.ranking.infrastructure.secondary.dao.settings.MongoSettings
import io.ktor.server.config.ApplicationConfig
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    singleOf(::PlayerDao) { bind<PlayerPort>() }
    singleOf(::PlayerManagement)
}

fun mongoSettings(config: ApplicationConfig) = module {
    single {
        MongoSettings(
            user = config.getMandatoryProperty("mongo.user"),
            password = config.getMandatoryProperty("mongo.password"),
            host = config.getMandatoryProperty("mongo.host"),
            database = config.getMandatoryProperty("mongo.database")
        )
    }
}

private fun ApplicationConfig.getMandatoryProperty(name: String) =
    checkNotNull(this.propertyOrNull(name)?.getString(), { "Missing property $name" })