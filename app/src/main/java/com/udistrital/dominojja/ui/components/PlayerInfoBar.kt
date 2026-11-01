package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.udistrital.dominojja.model.Player
import com.udistrital.dominojja.ui.theme.PlayerColors

@Composable
fun PlayerInfoBar(
    players: List<Player>,
    currentId: String?,
    territoryCount: (String) -> Int
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        players.forEachIndexed { index, p ->
            val color = PlayerColors[index % PlayerColors.size]
            val isCurrent = p.id == currentId
            Card(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (p.hasLeft) 0.4f else 1f),
                colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.18f)),
                border = BorderStroke(if (isCurrent) 3.dp else 1.dp, if (isCurrent) color else color.copy(alpha = 0.4f))
            ) {
                Column(Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(24.dp).background(color, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                p.name.firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        Text(
                            p.name,
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text("${p.score} pts", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "🏴 ${territoryCount(p.id)} · 🎲 ${p.hand.size}",
                        style = MaterialTheme.typography.labelSmall
                    )
                    if (isCurrent) {
                        Text("TURNO", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
                    } else if (p.hasLeft) {
                        Text("salió", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}