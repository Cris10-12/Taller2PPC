package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.logic.GameEngine
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.GameStatus
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.ui.components.PlayerInfoBar
import com.udistrital.dominojja.ui.components.TerritoryCard
import com.udistrital.dominojja.ui.components.TileView
import com.udistrital.dominojja.ui.theme.PlayerColors
import com.udistrital.dominojja.viewModel.GameViewModel


@Composable
fun GameScreen(viewModel: GameViewModel, onExit: () -> Unit) {
    val state by viewModel.state.collectAsState()
    val selected by viewModel.selectedTile.collectAsState()
    val current = state.currentPlayer
    val hasMove = GameEngine.hasMove(state)
    val playing = state.status == GameStatus.PLAYING

    AppScaffold(
        title = "Ronda ${state.round}/${GameConfig.MAX_GAME_ROUNDS}",
        onBack = { viewModel.leave(); onExit() }
    ) {
        PlayerInfoBar(
            players = state.players,
            currentId = current?.id,
            territoryCount = { GameEngine.territoriesOwned(state, it) }
        )
        Spacer(Modifier.height(8.dp))
        Text("Turno de ${current?.name ?: "-"}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.territories, key = { it.id }) { t ->
                val ownerIndex = state.players.indexOfFirst { it.id == t.ownerId }
                TerritoryCard(
                    territory = t,
                    ownerName = state.players.getOrNull(ownerIndex)?.name,
                    ownerColor = if (ownerIndex >= 0) PlayerColors[ownerIndex % PlayerColors.size] else null,
                    canPlayLeft = playing && selected?.let { GameEngine.canPlay(state, it, t.id, Side.LEFT) } == true,
                    canPlayRight = playing && selected?.let { GameEngine.canPlay(state, it, t.id, Side.RIGHT) } == true,
                    onPlay = { side -> viewModel.playOn(t.id, side) }
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("Mano de ${current?.name ?: "-"}")
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            current?.hand?.forEach { tile ->
                TileView(tile, selected = tile == selected, onClick = { viewModel.selectTile(tile) })
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = viewModel::draw,
                enabled = playing && !hasMove && state.pool.isNotEmpty()
            ) { Text("Robar (${state.pool.size})") }
            OutlinedButton(
                onClick = viewModel::pass,
                enabled = playing && !hasMove && state.pool.isEmpty()
            ) { Text("Pasar") }
            OutlinedButton(onClick = viewModel::restart) { Text("Reiniciar") }
        }
    }

    if (state.status == GameStatus.FINISHED) {
        val winner = state.players.find { it.id == state.winnerId }
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Fin de la partida") },
            text = {
                Column {
                    Text(winner?.let { "Ganador: ${it.name}" } ?: "Empate")
                    state.players.sortedByDescending { it.score }.forEach {
                        Text("${it.name}: ${it.score} pts · ${GameEngine.territoriesOwned(state, it.id)} territorios")
                    }
                }
            },
            confirmButton = { TextButton(onClick = viewModel::restart) { Text("Reiniciar") } },
            dismissButton = { TextButton(onClick = onExit) { Text("Salir") } }
        )
    }
}