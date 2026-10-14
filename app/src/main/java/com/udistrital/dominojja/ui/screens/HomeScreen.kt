package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.ui.components.MenuButton
import com.udistrital.dominojja.common.Spacing

@Composable
fun HomeScreen(
    onPlay: () -> Unit,
    onCreateRoom: () -> Unit,
    onJoinRoom: () -> Unit,
    onSettings: () -> Unit,
    onHowToPlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Dominó Territorios", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(Spacing.xlarge))
        MenuButton("Jugar", onPlay)
        Spacer(Modifier.height(Spacing.small))
        MenuButton("Crear sala", onCreateRoom)
        Spacer(Modifier.height(Spacing.small))
        MenuButton("Unirse a sala", onJoinRoom)
        Spacer(Modifier.height(Spacing.small))
        MenuButton("Configuración", onSettings)
        Spacer(Modifier.height(Spacing.small))
        MenuButton("Cómo jugar", onHowToPlay)
    }
}