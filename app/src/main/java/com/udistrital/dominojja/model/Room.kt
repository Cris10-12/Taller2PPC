package com.udistrital.dominojja.model

import com.udistrital.dominojja.common.Constants

data class Room(
    val id: String = "",
    val hostId: String = "",
    val players: List<String> = emptyList(),
    val status: String = Constants.STATUS_WAITING,
    val createdAt: Long = System.currentTimeMillis()
)