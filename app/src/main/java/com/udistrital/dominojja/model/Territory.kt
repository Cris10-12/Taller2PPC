package com.udistrital.dominojja.model

data class Territory(
    val id: Int,
    val effect: TerritoryEffect = TerritoryEffect.NONE,
    val ownerId: String? = null,
    val conquests: Int = 0,
    val portalTargetId: Int? = null,
    val tiles: List<Tile> = emptyList()
)