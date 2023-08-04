package com.jicay.ranking.domain

import com.jicay.ranking.domain.entity.Player
import com.jicay.ranking.domain.exception.AlreadyExistsException
import com.jicay.ranking.domain.exception.PlayerNotExistException
import com.jicay.ranking.domain.port.PlayerPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PlayerManagementTest {
    @InjectMockKs
    private lateinit var playerManagement: PlayerManagement

    @MockK
    private lateinit var playerPort: PlayerPort

    @Test
    fun `get player by name`() {
        every { playerPort.findPlayer(Player.Name("toto")) } returns Player(Player.Name("toto"), Player.Points(5))
        every { playerPort.getNumberOfBestPlayersByPoints(Player.Points(5)) } returns 4

        val res = playerManagement.getPlayer(Player.Name("toto"))

        val expected = Player(Player.Name("toto"), Player.Points(5)).also { it.ranking = Player.Ranking(5) }

        assertThat(res).isEqualTo(expected)
    }

    @Test
    fun `get null when no player`() {
        every { playerPort.findPlayer(Player.Name("toto")) } returns null

        val res = playerManagement.getPlayer(Player.Name("toto"))

        assertThat(res).isNull()
    }

    @Test
    fun `create new player`() {
        every { playerPort.savePlayer(Player(Player.Name("toto"))) } returns Player.Name("toto")

        val res = playerManagement.createPlayer(Player.Name("toto"))

        assertThat(res).isEqualTo(Player.Name("toto"))
    }

    @Test
    fun `create new player should throw an error when already exist`() {
        every { playerPort.savePlayer(Player(Player.Name("toto"))) } throws AlreadyExistsException("Error", null)

        assertThrows<AlreadyExistsException> { playerManagement.createPlayer(Player.Name("toto")) }
    }

    @Test
    fun `get full ranking`() {
        every { playerPort.getAllPlayerOrderByPoints() } returns listOf(
            Player(Player.Name("name1"), Player.Points(24)),
            Player(Player.Name("name2"), Player.Points(24)),
            Player(Player.Name("name3"), Player.Points(20)),
            Player(Player.Name("name4"), Player.Points(19)),
            Player(Player.Name("name5"), Player.Points(15)),
            Player(Player.Name("name6"), Player.Points(15)),
            Player(Player.Name("name7"), Player.Points(15)),
            Player(Player.Name("name8"), Player.Points(4))
        )

        val res = playerManagement.getFullRanking()

        val expected = listOf(
            Player(Player.Name("name1"), Player.Points(24)).also { it.ranking = Player.Ranking(1) },
            Player(Player.Name("name2"), Player.Points(24)).also { it.ranking = Player.Ranking(1) },
            Player(Player.Name("name3"), Player.Points(20)).also { it.ranking = Player.Ranking(3) },
            Player(Player.Name("name4"), Player.Points(19)).also { it.ranking = Player.Ranking(4) },
            Player(Player.Name("name5"), Player.Points(15)).also { it.ranking = Player.Ranking(5) },
            Player(Player.Name("name6"), Player.Points(15)).also { it.ranking = Player.Ranking(5) },
            Player(Player.Name("name7"), Player.Points(15)).also { it.ranking = Player.Ranking(5) },
            Player(Player.Name("name8"), Player.Points(4)).also { it.ranking = Player.Ranking(8) }
        )

        assertThat(res).containsExactlyElementsOf(expected)
    }

    @Test
    fun `update score of player`() {
        justRun { playerPort.updateScore(Player.Name("toto"), Player.Points(8)) }
        every { playerPort.findPlayer(Player.Name("toto")) } returns Player(Player.Name("toto"), Player.Points(8))
        every { playerPort.getNumberOfBestPlayersByPoints(Player.Points(8)) } returns 0

        val res = playerManagement.updateScore(Player.Name("toto"), Player.Points(8))

        val expected = Player(Player.Name("toto"), Player.Points(8)).also { it.ranking = Player.Ranking(1) }

        assertThat(res).isEqualTo(expected)
    }

    @Test
    fun `update score of player should throw error when there is not matching player`() {
        justRun { playerPort.updateScore(Player.Name("toto"), Player.Points(8)) }
        every { playerPort.findPlayer(Player.Name("toto")) } returns null

        assertThrows<PlayerNotExistException> { playerManagement.updateScore(Player.Name("toto"), Player.Points(8)) }
    }

    @Test
    fun `delete all players`() {
        justRun { playerPort.deleteAllPlayers() }

        playerManagement.deleteAllPlayers()

        verify(exactly = 1) { playerPort.deleteAllPlayers() }
    }
}