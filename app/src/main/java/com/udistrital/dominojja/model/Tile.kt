package com.udistrital.dominojja.model

data class Tile(val left: Int, val right: Int) {
    val isDouble: Boolean get() = left == right
    val total: Int get() = left + right
    fun flipped(): Tile = Tile(right, left)
    fun hasValue(value: Int): Boolean = left == value || right == value
}