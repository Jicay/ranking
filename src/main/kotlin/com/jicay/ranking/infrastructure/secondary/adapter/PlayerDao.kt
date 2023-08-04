package com.jicay.ranking.infrastructure.secondary.adapter

import com.jicay.ranking.domain.entity.Player
import com.jicay.ranking.domain.exception.AlreadyExistsException
import com.jicay.ranking.domain.port.PlayerPort
import com.jicay.ranking.infrastructure.secondary.dao.model.PlayerEntity
import com.jicay.ranking.infrastructure.secondary.dao.model.toEntity
import com.jicay.ranking.infrastructure.secondary.dao.settings.MongoSettings
import com.mongodb.ConnectionString
import com.mongodb.MongoWriteException
import com.mongodb.client.MongoCollection
import org.litote.kmongo.KMongo
import org.litote.kmongo.deleteMany
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.gt
import org.litote.kmongo.setValue

class PlayerDao(mongoSettings: MongoSettings): PlayerPort {
    private val players: MongoCollection<PlayerEntity>

    init {
        val client = KMongo.createClient(ConnectionString(computeConnectionString(mongoSettings)))
        val database = client.getDatabase(mongoSettings.database)
        players = database.getCollection<PlayerEntity>("player")
    }

    override fun findPlayer(name: Player.Name): Player? {
        return players.findOne(PlayerEntity::name eq name.value)?.toDomain()
    }

    override fun getNumberOfBestPlayersByPoints(points: Player.Points): Int {
        return players.countDocuments(
            PlayerEntity::points gt points.value
        ).toInt()
    }

    override fun savePlayer(player: Player): Player.Name {
        try {
            players.insertOne(player.toEntity())
        } catch (e: MongoWriteException) {
            throw AlreadyExistsException("Player ${player.name} already exists", e)
        }
        return player.name
    }

    override fun getAllPlayerOrderByPoints(): List<Player> {
        return players.find().sort(descending(PlayerEntity::points))
            .map { it.toDomain() }
            .toList()
    }

    override fun updateScore(name: Player.Name, points: Player.Points) {
        players.updateOne(PlayerEntity::name eq name.value, setValue(PlayerEntity::points, points.value))
    }

    override fun deleteAllPlayers() {
        players.deleteMany()
    }

    private fun computeConnectionString(mongoSettings: MongoSettings): String {
        return if (mongoSettings.user.isBlank()) {
            "mongodb://${mongoSettings.host}"
        } else {
            "mongodb://${mongoSettings.user}:${mongoSettings.password}@${mongoSettings.host}"
        }
    }
}