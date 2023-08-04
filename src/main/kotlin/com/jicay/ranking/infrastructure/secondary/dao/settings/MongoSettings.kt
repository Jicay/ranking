package com.jicay.ranking.infrastructure.secondary.dao.settings

data class MongoSettings(
    val user: String,
    val password: String,
    val host: String,
    val database: String
)
