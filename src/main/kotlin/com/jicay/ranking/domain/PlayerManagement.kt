package com.jicay.ranking.domain

import com.jicay.ranking.domain.entity.Player
import com.jicay.ranking.domain.exception.PlayerNotExistException
import com.jicay.ranking.domain.port.PlayerPort

class PlayerManagement(
    private val playerPort: PlayerPort
) {
    fun getPlayer(name: Player.Name): Player? {
        return playerPort
            .findPlayer(name)
            ?.also { it.ranking = Player.Ranking(playerPort.getNumberOfBestPlayersByPoints(it.points) + 1) }
    }

    fun createPlayer(name: Player.Name): Player.Name {
        return playerPort.savePlayer(Player(name))
    }

    fun getFullRanking(): List<Player> {
        var previousPoints: Int? = null
        var previousRank = 0
        return playerPort.getAllPlayerOrderByPoints().mapIndexed { index, player ->
            if (player.points.value != previousPoints) {
                previousPoints = player.points.value
                previousRank = index + 1
            }
            player.apply { ranking = Player.Ranking(previousRank) }

        }
    }

    fun updateScore(name: Player.Name, points: Player.Points): Player {
        playerPort.updateScore(name, points)
        return getPlayer(name) ?: throw PlayerNotExistException("Player ${name.value} does not exist")
    }

    fun deleteAllPlayers() {
        playerPort.deleteAllPlayers()
    }
}