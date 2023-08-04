package com.jicay.ranking.infrastructure.secondary.dao.config

import com.jicay.ranking.infrastructure.secondary.dao.settings.MongoSettings
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase
import liquibase.resource.DirectoryResourceAccessor
import java.io.File


fun migrateDatabaseSchema(mongoSettings: MongoSettings){
    val database = DatabaseFactory.getInstance()
        .openDatabase(
            "mongodb://${mongoSettings.host}/${mongoSettings.database}?authSource=admin",
            mongoSettings.user,
            mongoSettings.password,
            null,
            MongoLiquibaseDatabase::class.java.name,
            null,
            null,
            null
        )
    val liquibase = Liquibase(
        "/db/changelog.xml",
        DirectoryResourceAccessor(File("src/main/resources")),
        database
    )
    liquibase.update("")
    liquibase.close()
}