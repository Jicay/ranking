package com.jicay.ranking.infrastructure.application.plugins

import com.jicay.ranking.infrastructure.primary.web.playerRouting
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        playerRouting()
        openAPI(path="openapi", swaggerFile = "openapi/documentation.yaml")
        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
    }
}
