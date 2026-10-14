package com.udistrital.dominojja.model

data class GameState(
    val players: List<Player> = emptyList(),
    val territories: List<Territory> = emptyList(),
    val pool: List<Tile> = emptyList(),
    val currentPlayerIndex: Int = 0,
    val status: GameStatus = GameStatus.WAITING,
    val winnerId: String? = null,
    val consecutivePasses: Int = 0
) {
    val currentPlayer: Player? get() = players.getOrNull(currentPlayerIndex)
}