package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import com.udistrital.dominojja.common.Spacing
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.ui.components.MenuButton
import com.udistrital.dominojja.viewModel.WaitingRoomViewModel


@Composable
fun WaitingRoomScreen(
    code: String,
    onBack: () -> Unit,
    onGameStarted: () -> Unit,
    vm: WaitingRoomViewModel = viewModel()
) {
    val room by vm.room.collectAsState()
    val myId by vm.myId.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(code) { vm.observe(code) }
    LaunchedEffect(room?.status) {
        if (room?.status == "playing") onGameStarted()
    }

    AppScaffold(title = "Sala de espera", onBack = onBack) {
        Text("Código de sala", style = MaterialTheme.typography.titleMedium)
        Text(code, style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(Spacing.large))

        val r = room
        if (r == null) {
            Text("Conectando")
        } else {
            Text("Jugadores (${r.players.size}/${r.maxPlayers})",
                style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Spacing.small))
            r.players.forEach { id ->
                val label = r.playerNames[id] ?: "Jugador"
                Text(if (id == r.hostId) "$label (anfitrión)" else label)
            }
            Spacer(Modifier.height(Spacing.large))

            if (r.status == "playing") {
                Text("¡La partida ha comenzado!")
            } else if (myId == r.hostId) {
                MenuButton("Iniciar partida", {
                    if (r.players.size >= 2) vm.start()
                })
                if (r.players.size < 2) Text("Esperando más jugadores")
            } else {
                Text("Esperando a que el anfitrión inicie")
            }
        }

        error?.let {
            Spacer(Modifier.height(Spacing.medium))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}