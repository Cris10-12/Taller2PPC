package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.common.Spacing
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.ui.components.MenuButton
import com.udistrital.dominojja.ui.components.PlayerCountSelector
import com.udistrital.dominojja.viewModel.LobbyViewModel

@Composable
fun CreateRoomScreen(
    onBack: () -> Unit,
    onRoomCreated: (String) -> Unit = {},
    lobbyViewModel: LobbyViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(GameConfig.DEFAULT_PLAYERS) }

    val loading by lobbyViewModel.loading.collectAsState()
    val error by lobbyViewModel.error.collectAsState()
    val joinedRoom by lobbyViewModel.joinedRoom.collectAsState()

    // Cuando la sala se crea, avisamos a quien navega
    LaunchedEffect(joinedRoom) {
        joinedRoom?.let {
            onRoomCreated(it)
            lobbyViewModel.onNavigated()
        }
    }

    AppScaffold(title = "Crear sala", onBack = onBack) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Tu nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.large))
        PlayerCountSelector(selected = players, onSelect = { players = it })
        Spacer(Modifier.height(Spacing.large))
        MenuButton(
            if (loading) "Creando..." else "Crear sala",
            {
                if (!loading && name.isNotBlank()) {
                    lobbyViewModel.createRoom(name, players)
                }
            }
        )
        error?.let {
            Spacer(Modifier.height(Spacing.medium))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}