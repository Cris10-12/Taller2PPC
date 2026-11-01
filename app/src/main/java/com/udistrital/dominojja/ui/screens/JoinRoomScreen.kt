package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udistrital.dominojja.common.Spacing
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.ui.components.MenuButton
import com.udistrital.dominojja.viewModel.LobbyViewModel

@Composable
fun JoinRoomScreen(
    onBack: () -> Unit,
    onJoined: (String) -> Unit = {},
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }

    val loading by lobbyViewModel.loading.collectAsState()
    val error by lobbyViewModel.error.collectAsState()
    val joinedRoom by lobbyViewModel.joinedRoom.collectAsState()
    val rooms by lobbyViewModel.rooms.collectAsState()

    LaunchedEffect(joinedRoom) {
        joinedRoom?.let {
            onJoined(it)
            lobbyViewModel.onNavigated()
        }
    }

    AppScaffold(title = "Unirse a sala", onBack = onBack) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.medium))
        OutlinedTextField(
            value = code,
            onValueChange = { code = it.uppercase().take(5) },
            label = { Text("Código de sala") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.large))
        MenuButton(
            if (loading) "Uniéndose..." else "Unirse",
            {
                if (!loading && name.isNotBlank() && code.length == 5) {
                    lobbyViewModel.joinRoom(code, name)
                }
            }
        )
        error?.let {
            Spacer(Modifier.height(Spacing.medium))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // 10.B: salas abiertas
        Spacer(Modifier.height(Spacing.large))
        Text("Salas disponibles", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(Spacing.small))
        if (rooms.isEmpty()) Text("No hay salas abiertas por ahora")
        rooms.forEach { r ->
            Text(
                "${r.hostName} · ${r.players.size}/${r.maxPlayers} · ${r.id}",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { code = r.id }
                    .padding(vertical = 8.dp)
            )
        }
    }
}