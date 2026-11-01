package com.udistrital.dominojja.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.dominojja.data.AuthRepository
import com.udistrital.dominojja.data.RoomRepository
import com.udistrital.dominojja.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LobbyViewModel(
    private val auth: AuthRepository = AuthRepository(),
    private val repo: RoomRepository = RoomRepository()
) : ViewModel() {

    private val _rooms = MutableStateFlow<List<Room>>(emptyList())
    val rooms: StateFlow<List<Room>> = _rooms

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Código de la sala a la que se entró; la UI navega cuando deja de ser null
    private val _joinedRoom = MutableStateFlow<String?>(null)
    val joinedRoom: StateFlow<String?> = _joinedRoom

    init {
        viewModelScope.launch {
            repo.observeWaitingRooms()
                .catch { _error.value = it.message }
                .collect { _rooms.value = it }
        }
    }

    fun createRoom() = viewModelScope.launch {
        _loading.value = true
        val uid = auth.ensureSignedIn()
        repo.createRoom(uid)
            .onSuccess { _joinedRoom.value = it }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }

    fun joinRoom(code: String) = viewModelScope.launch {
        _loading.value = true
        val uid = auth.ensureSignedIn()
        repo.joinRoom(code.trim().uppercase(), uid)
            .onSuccess { _joinedRoom.value = code.trim().uppercase() }
            .onFailure { _error.value = it.message }
        _loading.value = false
    }

    fun clearError() { _error.value = null }
    fun onNavigated() { _joinedRoom.value = null }
}