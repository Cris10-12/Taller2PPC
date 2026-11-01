package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.logic.GameEngine
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.GameStatus
import com.udistrital.dominojja.model.Tile
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
    val myId by viewModel.myId.collectAsState()
    val isHost by viewModel.isHost.collectAsState()
    val syncError by viewModel.syncError.collectAsState()

    val current = state.currentPlayer
    val viewer = viewModel.viewerOf(state)
    val myTurn = viewModel.isMyTurn()
    val hasMove = GameEngine.hasMove(state)
    val playing = state.status == GameStatus.PLAYING
    val canRestart = !viewModel.isOnline || isHost
    val currentColor = PlayerColors[state.currentPlayerIndex.coerceAtLeast(0) % PlayerColors.size]

    // una ficha es jugable solo si encaja en algún extremo de algún territorio
    val playable: (Tile) -> Boolean = { tile ->
        state.territories.any {
            GameEngine.canPlay(state, tile, it.id, Side.LEFT) ||
                    GameEngine.canPlay(state, tile, it.id, Side.RIGHT)
        }
    }

    val hint = when {
        !playing -> ""
        !myTurn -> "Esperando a ${current?.name ?: "..."}…"
        hasMove && selected == null -> "Elige una ficha de tu mano"
        hasMove -> "Toca un territorio resaltado en verde para colocarla"
        state.pool.isNotEmpty() -> "No tienes jugada: roba una ficha del pozo"
        else -> "No tienes jugada y el pozo está vacío: pasa el turno"
    }

    AppScaffold(
        title = "Ronda ${state.round}/${GameConfig.MAX_GAME_ROUNDS}",
        onBack = { viewModel.leave(); onExit() }
    ) {
        // amrcador
        PlayerInfoBar(
            players = state.players,
            currentId = current?.id,
            territoryCount = { GameEngine.territoriesOwned(state, it) }
        )
        Spacer(Modifier.height(8.dp))

        // indica turno
        Box(
            Modifier
                .fillMaxWidth()
                .background(currentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .border(BorderStroke(2.dp, currentColor), RoundedCornerShape(12.dp))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            val title = if (viewModel.isOnline && myTurn) "¡Es tu turno!" else "Turno de ${current?.name ?: "-"}"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (hint.isNotEmpty()) Text(hint, style = MaterialTheme.typography.bodySmall)
            }
        }
        syncError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(8.dp))

        // el tablero dos por fila
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.territories.chunked(2), key = { it.first().id }) { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pair.forEach { t ->
                        val ownerIndex = state.players.indexOfFirst { it.id == t.ownerId }
                        TerritoryCard(
                            modifier = Modifier.weight(1f),
                            territory = t,
                            ownerName = state.players.getOrNull(ownerIndex)?.name,
                            ownerColor = if (ownerIndex >= 0) PlayerColors[ownerIndex % PlayerColors.size] else null,
                            canPlayLeft = myTurn && playing &&
                                    selected?.let { GameEngine.canPlay(state, it, t.id, Side.LEFT) } == true,
                            canPlayRight = myTurn && playing &&
                                    selected?.let { GameEngine.canPlay(state, it, t.id, Side.RIGHT) } == true,
                            onPlay = { side -> viewModel.playOn(t.id, side) }
                        )
                    }
                    if (pair.size < 2) Spacer(Modifier.weight(1f))
                }
            }
        }

        // mano o parte del jugador de este mismo dispositivo
        Spacer(Modifier.height(8.dp))
        Text("Tus fichas · ${viewer?.name ?: "-"}", style = MaterialTheme.typography.labelLarge)
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(top = 12.dp, bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            viewer?.hand?.forEach { tile ->
                TileView(
                    tile = tile,
                    height = 56.dp,
                    selected = tile == selected,
                    dimmed = !myTurn || !playing || !playable(tile),
                    onClick = { viewModel.selectTile(tile) }
                )
            }
        }

        // acciones
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = viewModel::draw,
                enabled = myTurn && playing && !hasMove && state.pool.isNotEmpty()
            ) { Text("🎲 Robar del pozo (${state.pool.size})") }
            OutlinedButton(
                onClick = viewModel::pass,
                enabled = myTurn && playing && !hasMove && state.pool.isEmpty()
            ) { Text("Pasar") }
            if (canRestart) {
                OutlinedButton(onClick = viewModel::restart) { Text("Reiniciar") }
            }
        }
    }

    if (state.status == GameStatus.FINISHED) {
        val winner = state.players.find { it.id == state.winnerId }
        AlertDialog(
            onDismissRequest = {},
            title = { Text(if (winner != null) "🏆 ¡Ganó ${winner.name}!" else "🤝 Empate") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    state.players.sortedByDescending { it.score }.forEach {
                        Text("${it.name}: ${it.score} pts · ${GameEngine.territoriesOwned(state, it.id)} territorios")
                    }
                }
            },
            confirmButton = {
                if (canRestart) TextButton(onClick = viewModel::restart) { Text("Jugar de nuevo") }
            },
            dismissButton = { TextButton(onClick = onExit) { Text("Volver al menú") } }
        )
    }
}