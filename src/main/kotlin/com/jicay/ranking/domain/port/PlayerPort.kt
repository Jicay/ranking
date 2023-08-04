package com.jicay.ranking.domain.port

import com.jicay.ranking.domain.entity.Player

interface PlayerPort {
    fun findPlayer(name: Player.Name): Player?
    fun getNumberOfBestPlayersByPoints(points: Player.Points): Int
    fun savePlayer(player: Player): Player.Name
    fun getAllPlayerOrderByPoints(): List<Player>
    fun updateScore(name: Player.Name, points: Player.Points)
    fun deleteAllPlayers()
}