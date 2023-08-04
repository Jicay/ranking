package com.jicay.ranking.domain.entity

import com.jicay.ranking.domain.exception.InvalidDomainException
import kotlin.properties.Delegates

data class Player(
    val name: Name,
    val points: Points = Points()
) {
    var ranking by Delegates.notNull<Ranking>()

    @JvmInline
    value class Name(val value: String) {
        init {
            if (value.isBlank()) {
                throw InvalidDomainException("Name should not be empty")
            }
        }
    }

    @JvmInline
    value class Points(val value: Int = 0) {
        init {
            if (value < 0) {
                throw InvalidDomainException("Points must be positive or zero")
            }
        }
    }

    @JvmInline
    value class Ranking(val value: Int)
}
