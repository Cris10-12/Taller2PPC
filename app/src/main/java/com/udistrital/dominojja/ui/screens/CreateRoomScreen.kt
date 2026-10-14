package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.ui.components.MenuButton
import com.udistrital.dominojja.ui.components.PlayerCountSelector
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.common.Spacing

@Composable
fun CreateRoomScreen(onBack: () -> Unit, onCreate: (String, Int) -> Unit) {
    var name by remember { mutableStateOf("") }
    var players by remember { mutableStateOf(GameConfig.DEFAULT_PLAYERS) }
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
        MenuButton("Crear sala", { onCreate(name, players) })
    }
}