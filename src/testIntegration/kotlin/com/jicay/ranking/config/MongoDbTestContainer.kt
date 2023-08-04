package com.jicay.ranking.config

import com.jicay.ranking.infrastructure.secondary.dao.model.PlayerEntity
import com.mongodb.ConnectionString
import com.mongodb.client.MongoCollection
import io.ktor.server.config.*
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName


class MongoDbTestContainer {

    val mongoDbContainer = MongoDBContainer(
        DockerImageName.parse("library/mongo:5.0.7")
            .asCompatibleSubstituteFor("mongo")
    )

    val collection: MongoCollection<PlayerEntity>

    init {
        mongoDbContainer.waitingFor(Wait.forListeningPort())
        mongoDbContainer.start()


        val client = KMongo.createClient(ConnectionString(mongoDbContainer.connectionString))
        val database = client.getDatabase("test")
        collection = database.getCollection<PlayerEntity>("player")
    }

    fun getConfig(): MapApplicationConfig {
        return MapApplicationConfig(
            "mongo.host" to "${mongoDbContainer.host}:${mongoDbContainer.firstMappedPort}",
            "mongo.user" to "",
            "mongo.password" to "",
            "mongo.database" to "test"
        )
    }

    fun drop() {
        collection.drop()
    }
}