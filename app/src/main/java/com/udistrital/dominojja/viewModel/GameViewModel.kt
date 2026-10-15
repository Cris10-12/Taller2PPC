package com.udistrital.dominojja.viewModel

import androidx.lifecycle.ViewModel
import com.udistrital.dominojja.logic.GameEngine
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.GameState
import com.udistrital.dominojja.model.GameStatus
import com.udistrital.dominojja.model.Tile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _selectedTile = MutableStateFlow<Tile?>(null)
    val selectedTile: StateFlow<Tile?> = _selectedTile.asStateFlow()

    fun startLocal(playerCount: Int) {
        if (_state.value.status != GameStatus.WAITING) return
        _state.value = GameEngine.newGame((1..playerCount).map { "p$it" to "Jugador $it" })
    }

    fun selectTile(tile: Tile) {
        if (_state.value.status != GameStatus.PLAYING) return
        _selectedTile.value = if (_selectedTile.value == tile) null else tile
    }

    fun playOn(territoryId: Int, side: Side) {
        val tile = _selectedTile.value ?: return
        _state.update { GameEngine.playTile(it, tile, territoryId, side) }
        _selectedTile.value = null
    }

    fun draw() {
        _state.update { GameEngine.drawFromPool(it) }
    }

    fun pass() {
        _state.update { GameEngine.pass(it) }
        _selectedTile.value = null
    }

    fun leave() {
        val id = _state.value.currentPlayer?.id ?: return
        _state.update { GameEngine.leave(it, id) }
        _selectedTile.value = null
    }

    fun restart() {
        _state.update { GameEngine.restart(it) }
        _selectedTile.value = null
    }
}