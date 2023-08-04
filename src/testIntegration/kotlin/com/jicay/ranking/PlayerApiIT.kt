package com.jicay.ranking

import com.jicay.ranking.config.MongoDbTestContainer
import com.jicay.ranking.infrastructure.application.module
import com.jicay.ranking.infrastructure.primary.web.dto.PlayerCreationDTO
import com.jicay.ranking.infrastructure.primary.web.dto.PointsDTO
import com.jicay.ranking.infrastructure.secondary.dao.model.PlayerEntity
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.litote.kmongo.eq
import org.litote.kmongo.findOne

class PlayerApiIT {
    private val mongoDbTestContainer: MongoDbTestContainer

    init {
        mongoDbTestContainer = MongoDbTestContainer()
    }

    @BeforeEach
    fun beforeEach() {
        val collection = mongoDbTestContainer.collection
        collection.insertMany(listOf(
            PlayerEntity("toto", 3),
            PlayerEntity("john", 8),
            PlayerEntity("jane", 1),
            PlayerEntity("jeanmi", 5),
        ))
    }

    @AfterEach
    fun afterEach() {
        mongoDbTestContainer.drop()
    }

    @Test
    fun `should return not found when id does not exist`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        client.get("/players/tata").apply {
            assertThat(status).isEqualTo(HttpStatusCode.NotFound)
            assertThat(bodyAsText()).isEqualTo("No player with name tata")
        }
    }

    @Test
    fun `should return the player when id is found`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        client.get("/players/toto").apply {
            assertThat(status).isEqualTo(HttpStatusCode.OK)
            assertThat(bodyAsText()).isEqualTo(
                // language=json
                """
                    {
                        "name": "toto",
                        "ranking": 3,
                        "points": 3
                    }
                """.trimIndent())
        }
    }

    @Test
    fun `create player should save player in db`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerCreationDTO("leo"))
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.Created)
            assertThat(bodyAsText()).isEqualTo("""leo""")
        }

        val res = mongoDbTestContainer.collection.findOne(PlayerEntity::name eq "leo")

        val expected = PlayerEntity("leo", 0)

        assertThat(res).isEqualTo(expected)
    }

    @Test
    fun `create player should return an error when name is empty`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerCreationDTO(""))
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.BadRequest)
            assertThat(bodyAsText()).isEqualTo("""Name should not be empty""")
        }
    }

    @Test
    fun `create player should returns an error when already exists`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.post("/players") {
            contentType(ContentType.Application.Json)
            setBody(PlayerCreationDTO("toto"))
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.BadRequest)
            assertThat(bodyAsText()).isEqualTo("""Player toto already exists""")
        }
    }

    @Test
    fun `get full ranking should return all the players ordered by points`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        client.get("/players").apply {
            assertThat(status).isEqualTo(HttpStatusCode.OK)
            assertThat(bodyAsText()).isEqualTo(
                //language=json
                """
                    [
                        {
                            "name": "john",
                            "ranking": 1,
                            "points": 8
                        },
                        {
                            "name": "jeanmi",
                            "ranking": 2,
                            "points": 5
                        },
                        {
                            "name": "toto",
                            "ranking": 3,
                            "points": 3
                        },
                        {
                            "name": "jane",
                            "ranking": 4,
                            "points": 1
                        }
                    ]
                """.trimIndent()
            )
        }
    }

    @Test
    fun `update score of player should save the new points and returns the current player full info`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.patch("/players/toto") {
            contentType(ContentType.Application.Json)
            setBody(PointsDTO(12))
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.OK)
            assertThat(bodyAsText()).isEqualTo(
                // language=json
                """
                    {
                        "name": "toto",
                        "ranking": 1,
                        "points": 12
                    }
                """.trimIndent())
        }

        val res = mongoDbTestContainer.collection.findOne(PlayerEntity::name eq "toto")

        val expected = PlayerEntity(name = "toto", points = 12)

        assertThat(expected).isEqualTo(res)
    }

    @Test
    fun `update score of player should return an error when player does not exist`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.patch("/players/tata") {
            contentType(ContentType.Application.Json)
            setBody(PointsDTO(12))
        }.apply {
            assertThat(status).isEqualTo(HttpStatusCode.NotFound)
            assertThat(bodyAsText()).isEqualTo("Player tata does not exist")
        }
    }

    @Test
    fun `delete all players`() = testApplication {
        environment {
            config = mongoDbTestContainer.getConfig()
        }
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        client.delete("/players").apply {
            assertThat(status).isEqualTo(HttpStatusCode.NoContent)
        }

        val res = mongoDbTestContainer.collection.find()

        assertThat(res).isEmpty()
    }
}
