package com.udistrital.dominojja.model

import com.udistrital.dominojja.common.Constants

data class Room(
    val id: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val maxPlayers: Int = Constants.MAX_PLAYERS,
    val players: List<String> = emptyList(),
    val playerNames: Map<String, String> = emptyMap(),
    val status: String = Constants.STATUS_WAITING,
    val createdAt: Long = System.currentTimeMillis(),
    val gameStateJson: String = "",
    val version: Long = 0,
)