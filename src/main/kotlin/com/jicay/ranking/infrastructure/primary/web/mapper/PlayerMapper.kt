package com.jicay.ranking.infrastructure.primary.web.mapper

import com.jicay.ranking.domain.entity.Player
import com.jicay.ranking.infrastructure.primary.web.dto.PlayerDTO

fun Player.toDto(): PlayerDTO = PlayerDTO(
    name = name.value,
    ranking = ranking.value,
    points = points.value
)