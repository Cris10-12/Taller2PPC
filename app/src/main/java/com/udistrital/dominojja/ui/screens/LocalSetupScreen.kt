package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
fun LocalSetupScreen(onBack: () -> Unit, onStart: (Int) -> Unit) {
    var players by remember { mutableStateOf(GameConfig.DEFAULT_PLAYERS) }

    AppScaffold(title = "Jugar", onBack = onBack) {
        PlayerCountSelector(selected = players, onSelect = { players = it })
        Spacer(Modifier.height(Spacing.large))
        MenuButton("Iniciar partida", { onStart(players) })
    }
}