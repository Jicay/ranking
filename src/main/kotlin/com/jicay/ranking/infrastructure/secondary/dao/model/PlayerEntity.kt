package com.jicay.ranking.infrastructure.secondary.dao.model

import com.jicay.ranking.domain.entity.Player

data class PlayerEntity(
    val name: String,
    val points: Int
) {
    fun toDomain(): Player {
        return Player(name = Player.Name(name), points = Player.Points(points))
    }
}

fun Player.toEntity(): PlayerEntity {
    return PlayerEntity(this.name.value, this.points.value)
}
