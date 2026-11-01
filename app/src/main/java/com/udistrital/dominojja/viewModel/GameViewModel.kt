package com.udistrital.dominojja.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.udistrital.dominojja.data.AuthRepository
import com.udistrital.dominojja.data.RoomRepository
import com.udistrital.dominojja.logic.GameEngine
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.GameState
import com.udistrital.dominojja.model.GameStatus
import com.udistrital.dominojja.model.Tile
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.udistrital.dominojja.model.Player

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state: StateFlow<GameState> = _state.asStateFlow()

    private val _selectedTile = MutableStateFlow<Tile?>(null)
    val selectedTile: StateFlow<Tile?> = _selectedTile.asStateFlow()

    // online
    private val roomRepo = RoomRepository()
    private val authRepo = AuthRepository()
    private val gson = Gson()

    private var onlineCode: String? = null
    private var onlineVersion = 0L
    private var syncJob: Job? = null
    private var initialSent = false

    private val _myId = MutableStateFlow<String?>(null)
    /** uid de este dispositivo, null en modo local, la pantalla lo usa para saber cuál es "mi mano". */
    val myId: StateFlow<String?> = _myId.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    private val _isHost = MutableStateFlow(false)
    val isHost: StateFlow<Boolean> = _isHost.asStateFlow()
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    val isOnline: Boolean get() = onlineCode != null

    /** en local siempre es true, en online solo cuando el turno es de este dispositivo. */
    fun isMyTurn(): Boolean =

        !isOnline || _state.value.currentPlayer?.id == _myId.value
    /** jugador cuya mano se dibuja, en online es siempre el de este dispositivo, en local el del turno. */
    fun viewerOf(state: GameState): Player? =
        if (isOnline) state.players.find { it.id == _myId.value } else state.currentPlayer

    fun startOnline(code: String) {
        if (syncJob != null) return
        onlineCode = code
        syncJob = viewModelScope.launch {
            val uid = try {
                authRepo.ensureSignedIn()
            } catch (e: Exception) {
                _syncError.value = e.message
                return@launch
            }
            _myId.value = uid

            roomRepo.observeRoom(code)
                .catch { _syncError.value = it.message }
                .collect { room ->
                    if (room == null) return@collect
                    _isHost.value = (room.hostId == uid)

                    // Aún no hay partida solo el anfitrión
                    if (room.gameStateJson.isBlank()) {
                        if (uid == room.hostId && !initialSent) {
                            initialSent = true
                            val players = room.players.map { id ->
                                id to (room.playerNames[id] ?: "Jugador")
                            }
                            val initial = GameEngine.newGame(players)
                            roomRepo.publishState(code, gson.toJson(initial), room.version)
                                .onFailure {
                                    initialSent = false
                                    _syncError.value = it.message
                                }
                        }
                        return@collect
                    }


                    if (room.version > onlineVersion) {
                        val parsed = runCatching {
                            gson.fromJson(room.gameStateJson, GameState::class.java)
                        }.getOrNull()
                        if (parsed == null) {
                            _syncError.value = "No se pudo leer el estado de la partida"
                            return@collect
                        }
                        onlineVersion = room.version
                        _state.value = parsed
                        if (parsed.currentPlayer?.id != uid) _selectedTile.value = null
                    }
                }
        }
    }

    fun stopOnline() {
        syncJob?.cancel()
        syncJob = null
        onlineCode = null
        onlineVersion = 0
        initialSent = false
    }

    fun clearSyncError() { _syncError.value = null }

    /**
     * aplica una jugada y rn local actualiza el estado directo
     * en online lo publica en firestore y el listener lo devuelve a todos.
     */
    private fun apply(transform: (GameState) -> GameState) {
        val old = _state.value
        val new = transform(old)
        if (new === old) return              // jugada inválida asi que el motor devolvió el mismo estado

        val code = onlineCode
        if (code == null) {
            _state.value = new
            return
        }
        viewModelScope.launch {
            roomRepo.publishState(code, gson.toJson(new), onlineVersion)
                .onFailure { _syncError.value = it.message }
        }
    }


    fun startLocal(playerCount: Int) {
        if (_state.value.status != GameStatus.WAITING) return
        _state.value = GameEngine.newGame((1..playerCount).map { "p$it" to "Jugador $it" })
    }

    fun selectTile(tile: Tile) {
        if (_state.value.status != GameStatus.PLAYING) return
        if (!isMyTurn()) return
        _selectedTile.value = if (_selectedTile.value == tile) null else tile
    }

    fun playOn(territoryId: Int, side: Side) {
        val tile = _selectedTile.value ?: return
        if (!isMyTurn()) return
        apply { GameEngine.playTile(it, tile, territoryId, side) }
        _selectedTile.value = null
    }

    fun draw() {
        if (!isMyTurn()) return
        apply { GameEngine.drawFromPool(it) }
    }

    fun pass() {
        if (!isMyTurn()) return
        apply { GameEngine.pass(it) }
        _selectedTile.value = null
    }

    fun leave() {
        // en online "salir" siempre se refiere a mí aunque no sea mi turno
        val id = if (isOnline) _myId.value else _state.value.currentPlayer?.id
        if (id == null) return
        apply { GameEngine.leave(it, id) }
        _selectedTile.value = null
    }

    fun restart() {
        apply { GameEngine.restart(it) }
        _selectedTile.value = null
    }

    override fun onCleared() {
        stopOnline()
        super.onCleared()
    }
}