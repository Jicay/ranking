package com.jicay.ranking.infrastructure.primary.web

import com.jicay.ranking.domain.PlayerManagement
import com.jicay.ranking.domain.entity.Player
import com.jicay.ranking.domain.exception.AlreadyExistsException
import com.jicay.ranking.domain.exception.InvalidDomainException
import com.jicay.ranking.domain.exception.PlayerNotExistException
import com.jicay.ranking.infrastructure.primary.web.dto.PlayerCreationDTO
import com.jicay.ranking.infrastructure.primary.web.dto.PointsDTO
import com.jicay.ranking.infrastructure.primary.web.mapper.toDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

fun Route.playerRouting() {
    val playerManagementUseCase by inject<PlayerManagement>()

    route("/players") {
        get {
            val ranking = playerManagementUseCase.getFullRanking()
                .map { player -> player.toDto() }

            call.respond(ranking)
        }
        get("{name?}") {
            val name = call.parameters["name"] ?: return@get call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )
            val player = try {
                playerManagementUseCase.getPlayer(Player.Name(name)) ?: return@get call.respondText(
                    "No player with name $name",
                    status = HttpStatusCode.NotFound
                )
            } catch (e: InvalidDomainException) {
                return@get call.respondText(
                    e.message ?: "Invalid entity",
                    status = HttpStatusCode.BadRequest
                )
            }
            call.respond(player.toDto())
        }
        post {
            val player = call.receive<PlayerCreationDTO>()
            val name = try {
                playerManagementUseCase.createPlayer(Player.Name(player.name))
            } catch (e: AlreadyExistsException) {
                call.application.environment.log.warn("Player ${player.name} already exists", e)
                return@post call.respondText(
                    "Player ${player.name} already exists",
                    status = HttpStatusCode.BadRequest
                )
            } catch (e: InvalidDomainException) {
                return@post call.respondText(
                    e.message ?: "Invalid entity",
                    status = HttpStatusCode.BadRequest
                )
            }
            call.respondText(name.value, status = HttpStatusCode.Created)
        }
        patch("{name?}") {
            val name = call.parameters["name"] ?: return@patch call.respondText(
                "Missing name",
                status = HttpStatusCode.BadRequest
            )

            val points = call.receive<PointsDTO>()

            val updatedPlayer = try {
                playerManagementUseCase.updateScore(Player.Name(name), Player.Points(points.points))
            } catch (e: PlayerNotExistException) {
                call.application.environment.log.warn("Player ${name} does exist", e)
                return@patch call.respondText(
                    "Player ${name} does not exist",
                    status = HttpStatusCode.NotFound
                )
            } catch (e: InvalidDomainException) {
                return@patch call.respondText(
                    e.message ?: "Invalid entity",
                    status = HttpStatusCode.BadRequest
                )
            }

            call.respond(updatedPlayer.toDto())
        }
        delete {
            playerManagementUseCase.deleteAllPlayers()
            call.respond(status = HttpStatusCode.NoContent, message = "")
        }
    }
}