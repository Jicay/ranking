package com.jicay.ranking.infrastructure.primary.web.dto

import kotlinx.serialization.Serializable


@Serializable
data class PlayerDTO(
    // The name of the player
    val name: String,
    val ranking: Int,
    val points: Int
)

@Serializable
data class PlayerCreationDTO(
    val name: String
)

@Serializable
data class PointsDTO(
    val points: Int
)