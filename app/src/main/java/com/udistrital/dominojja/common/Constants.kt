package com.udistrital.dominojja.common

import androidx.compose.ui.unit.dp

object Spacing {
    val small = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val xlarge = 32.dp
}

object GameConfig {
    val PLAYER_OPTIONS = listOf(2, 3, 4)
    const val DEFAULT_PLAYERS = 2
    const val INITIAL_TILES = 7
    const val TREASURE_POINTS = 5
    const val TRAP_POINTS = -2
    const val TERRITORY_COUNT = 6
    const val BONUS_POINTS = 3
    const val FORTRESS_CONQUESTS = 2
}