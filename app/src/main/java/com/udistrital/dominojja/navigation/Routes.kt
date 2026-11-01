package com.udistrital.dominojja.navigation

object Routes {
    const val HOME = "home"
    const val LOCAL = "local"
    const val CREATE_ROOM = "create_room"
    const val JOIN_ROOM = "join_room"
    const val SETTINGS = "settings"
    const val HOW_TO_PLAY = "how_to_play"
    const val GAME = "game/{players}"
    fun game(players: Int) = "game/$players"
    const val WAITING_ROOM = "waiting_room/{code}"
    fun waitingRoom(code: String) = "waiting_room/$code"

    const val GAME_ONLINE = "game_online/{code}"

    fun gameOnline(code: String) = "game_online/$code"
}