package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.model.Player
import com.udistrital.dominojja.ui.theme.PlayerColors

@Composable
fun PlayerInfoBar(
    players: List<Player>,
    currentId: String?,
    territoryCount: (String) -> Int,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        players.forEachIndexed { i, p ->
            val color = PlayerColors[i % PlayerColors.size]
            val isCurrent = p.id == currentId
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = color.copy(alpha = if (isCurrent) 0.35f else 0.12f)
                ),
                border = if (isCurrent) BorderStroke(2.dp, color) else null
            ) {
                Column(Modifier.padding(8.dp)) {
                    Text(
                        if (p.hasLeft) "${p.name} (salió)" else p.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text("${p.score} pts")
                    Text("F: ${p.hand.size} | T: ${territoryCount(p.id)}")
                }
            }
        }
    }
}