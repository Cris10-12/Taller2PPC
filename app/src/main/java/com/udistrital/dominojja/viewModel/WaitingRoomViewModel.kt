package com.udistrital.dominojja.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udistrital.dominojja.data.AuthRepository
import com.udistrital.dominojja.data.RoomRepository
import com.udistrital.dominojja.model.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class WaitingRoomViewModel(
    private val auth: AuthRepository = AuthRepository(),
    private val repo: RoomRepository = RoomRepository()
) : ViewModel() {

    private val _room = MutableStateFlow<Room?>(null)
    val room: StateFlow<Room?> = _room

    private val _myId = MutableStateFlow<String?>(null)
    val myId: StateFlow<String?> = _myId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var job: Job? = null

    fun observe(code: String) {
        if (job != null) return
        job = viewModelScope.launch {
            _myId.value = runCatching { auth.ensureSignedIn() }.getOrNull()
            repo.observeRoom(code)
                .catch { _error.value = it.message }
                .collect { _room.value = it }
        }
    }

    fun start() {
        val code = _room.value?.id ?: return
        viewModelScope.launch {
            repo.startGame(code).onFailure { _error.value = it.message }
        }
    }
}