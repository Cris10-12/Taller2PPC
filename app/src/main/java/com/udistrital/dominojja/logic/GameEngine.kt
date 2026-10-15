package com.udistrital.dominojja.logic

import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.model.GameState
import com.udistrital.dominojja.model.GameStatus
import com.udistrital.dominojja.model.Player
import com.udistrital.dominojja.model.Territory
import com.udistrital.dominojja.model.TerritoryEffect
import com.udistrital.dominojja.model.Tile

object GameEngine {

    fun createDeck(): List<Tile> =
        (0..6).flatMap { l -> (l..6).map { r -> Tile(l, r) } }

    fun newGame(players: List<Pair<String, String>>): GameState {
        val deck = createDeck().shuffled()
        var index = 0
        val created = players.map { (id, name) ->
            val hand = deck.subList(index, index + GameConfig.INITIAL_TILES).toList()
            index += GameConfig.INITIAL_TILES
            Player(id = id, name = name, hand = hand)
        }
        return GameState(
            players = created,
            territories = createTerritories(),
            pool = deck.drop(index),
            status = GameStatus.PLAYING
        )
    }

    fun orient(territory: Territory, tile: Tile, side: Side): Tile? {
        if (territory.tiles.isEmpty()) return tile
        return when (side) {
            Side.LEFT -> {
                val end = territory.tiles.first().left
                when {
                    tile.right == end -> tile
                    tile.left == end -> tile.flipped()
                    else -> null
                }
            }
            Side.RIGHT -> {
                val end = territory.tiles.last().right
                when {
                    tile.left == end -> tile
                    tile.right == end -> tile.flipped()
                    else -> null
                }
            }
        }
    }

    fun canPlay(state: GameState, tile: Tile, territoryId: Int, side: Side): Boolean {
        val territory = state.territories.getOrNull(territoryId) ?: return false
        return orient(territory, tile, side) != null
    }

    fun hasMove(state: GameState): Boolean {
        val player = state.currentPlayer ?: return false
        return player.hand.any { tile ->
            state.territories.any { t ->
                orient(t, tile, Side.LEFT) != null || orient(t, tile, Side.RIGHT) != null
            }
        }
    }

    fun playTile(state: GameState, tile: Tile, territoryId: Int, side: Side): GameState {
        if (state.status != GameStatus.PLAYING) return state
        val player = state.currentPlayer ?: return state
        if (tile !in player.hand) return state
        val territory = state.territories.getOrNull(territoryId) ?: return state
        val oriented = orient(territory, tile, side) ?: return state

        val newTiles = if (territory.tiles.isEmpty()) {
            listOf(oriented)
        } else if (side == Side.LEFT) {
            listOf(oriented) + territory.tiles
        } else {
            territory.tiles + oriented
        }

        val conquests = territory.conquests + 1
        val conquered = territory.effect != TerritoryEffect.FORTRESS ||
                conquests >= GameConfig.FORTRESS_CONQUESTS
        val newTerritory = territory.copy(
            tiles = newTiles,
            ownerId = if (conquered) player.id else territory.ownerId,
            conquests = conquests
        )
        val newPlayer = player.copy(
            hand = player.hand - tile,
            score = player.score + tile.total + effectPoints(territory.effect)
        )
        val replaced = state.territories.map { if (it.id == territoryId) newTerritory else it }

        val updated = state.copy(
            players = state.players.map { if (it.id == player.id) newPlayer else it },
            territories = applyPortal(replaced, newTerritory, player.id),
            consecutivePasses = 0
        )

        return if (newPlayer.hand.isEmpty()) finish(updated) else nextTurn(updated)
    }

    fun drawFromPool(state: GameState): GameState {
        if (state.status != GameStatus.PLAYING) return state
        val player = state.currentPlayer ?: return state
        if (hasMove(state) || state.pool.isEmpty()) return state

        val drawn = state.pool.first()
        val newPlayer = player.copy(hand = player.hand + drawn)
        return state.copy(
            players = state.players.map { if (it.id == player.id) newPlayer else it },
            pool = state.pool.drop(1)
        )
    }

    fun pass(state: GameState): GameState {
        if (state.status != GameStatus.PLAYING) return state
        if (hasMove(state) || state.pool.isNotEmpty()) return state

        val updated = state.copy(consecutivePasses = state.consecutivePasses + 1)
        val active = updated.players.count { !it.hasLeft }
        return if (updated.consecutivePasses >= active) finish(updated) else nextTurn(updated)
    }

    private fun createTerritories(): List<Territory> {
        val effects = listOf(
            TerritoryEffect.NONE,
            TerritoryEffect.BONUS,
            TerritoryEffect.TREASURE,
            TerritoryEffect.TRAP,
            TerritoryEffect.FORTRESS,
            TerritoryEffect.PORTAL
        ).shuffled()
        val size = effects.size
        return effects.mapIndexed { index, effect ->
            Territory(
                id = index,
                effect = effect,
                portalTargetId = if (effect == TerritoryEffect.PORTAL) (index + 1) % size else null
            )
        }
    }

    private fun effectPoints(effect: TerritoryEffect): Int = when (effect) {
        TerritoryEffect.BONUS -> GameConfig.BONUS_POINTS
        TerritoryEffect.TREASURE -> GameConfig.TREASURE_POINTS
        TerritoryEffect.TRAP -> GameConfig.TRAP_POINTS
        else -> 0
    }

    private fun applyPortal(
        territories: List<Territory>,
        source: Territory,
        playerId: String
    ): List<Territory> {
        val targetId = source.portalTargetId
        if (source.effect != TerritoryEffect.PORTAL || targetId == null) return territories
        return territories.map {
            if (it.id == targetId && it.effect != TerritoryEffect.FORTRESS) it.copy(ownerId = playerId) else it
        }
    }

    fun territoriesOwned(state: GameState, playerId: String): Int =
        state.territories.count { it.ownerId == playerId }

    fun leave(state: GameState, playerId: String): GameState {
        if (state.status != GameStatus.PLAYING) return state
        if (state.players.none { it.id == playerId }) return state
        val wasCurrent = state.currentPlayer?.id == playerId
        val updated = state.copy(
            players = state.players.map { if (it.id == playerId) it.copy(hasLeft = true) else it }
        )
        if (updated.players.count { !it.hasLeft } <= 1) return finish(updated)
        return if (wasCurrent) nextTurn(updated) else updated
    }

    fun restart(state: GameState): GameState =
        newGame(state.players.map { it.id to it.name })

    private fun nextTurn(state: GameState): GameState {
        val size = state.players.size
        var next = (state.currentPlayerIndex + 1) % size
        var guard = 0
        while (state.players[next].hasLeft && guard < size) {
            next = (next + 1) % size
            guard++
        }
        val newRound = if (next <= state.currentPlayerIndex) state.round + 1 else state.round
        val advanced = state.copy(currentPlayerIndex = next, round = newRound)
        return if (newRound > GameConfig.MAX_GAME_ROUNDS) finish(advanced) else advanced
    }

    private fun finish(state: GameState): GameState =
        state.copy(status = GameStatus.FINISHED, winnerId = computeWinnerId(state))

    private fun computeWinnerId(state: GameState): String? {
        val candidates = state.players.filter { !it.hasLeft }.ifEmpty { state.players }
        val ranked = candidates.sortedWith(
            compareByDescending<Player> { it.score }
                .thenByDescending { territoriesOwned(state, it.id) }
        )
        val best = ranked.firstOrNull() ?: return null
        val tied = ranked.count {
            it.score == best.score &&
                    territoriesOwned(state, it.id) == territoriesOwned(state, best.id)
        } > 1
        return if (tied) null else best.id //null = empate
    }
}