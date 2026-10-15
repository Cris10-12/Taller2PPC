package com.udistrital.dominojja

import org.junit.Test
import com.udistrital.dominojja.logic.GameEngine
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.*
import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class GameEngineTest {
    private fun twoPlayers(
        aScore: Int = 0, bScore: Int = 0,
        handA: List<Tile> = listOf(Tile(1, 1)),
        handB: List<Tile> = listOf(Tile(1, 1)),
        territories: List<Territory>
    ) = GameState(
        players = listOf(
            Player("a", "A", handA, aScore),
            Player("b", "B", handB, bScore)
        ),
        territories = territories,
        status = GameStatus.PLAYING
    )

    @Test
    fun lastTileFinishesGameAndPicksWinnerByScore() {
        val s = twoPlayers(handA = listOf(Tile(6, 6)), territories = listOf(Territory(0)))
        val r = GameEngine.playTile(s, Tile(6, 6), 0, Side.RIGHT)
        assertEquals(GameStatus.FINISHED, r.status)
        assertEquals("a", r.winnerId)
    }

    @Test
    fun tieOnScoreIsBrokenByTerritories() {
        val t = Territory(0, ownerId = "a", tiles = listOf(Tile(0, 0)))
        val s = twoPlayers(aScore = 10, bScore = 10, territories = listOf(t))
            .copy(consecutivePasses = 1)
        val r = GameEngine.pass(s)
        assertEquals(GameStatus.FINISHED, r.status)
        assertEquals("a", r.winnerId)
    }

    @Test
    fun fullTieHasNoWinner() {
        val t = Territory(0, tiles = listOf(Tile(0, 0)))
        val s = twoPlayers(aScore = 5, bScore = 5, territories = listOf(t))
            .copy(consecutivePasses = 1)
        assertNull(GameEngine.pass(s).winnerId)
    }

    @Test
    fun leavingWithTwoPlayersEndsGameForTheOther() {
        val s = twoPlayers(aScore = 99, territories = listOf(Territory(0)))
        val r = GameEngine.leave(s, "a")
        assertEquals(GameStatus.FINISHED, r.status)
        assertEquals("b", r.winnerId)
    }

    @Test
    fun restartDealsNewHandsAndKeepsPlayers() {
        val s = twoPlayers(territories = listOf(Territory(0)))
        val r = GameEngine.restart(s)
        assertEquals(GameStatus.PLAYING, r.status)
        assertEquals(listOf("a", "b"), r.players.map { it.id })
        assertTrue(r.players.all { it.hand.size == 7 && it.score == 0 })
    }
}