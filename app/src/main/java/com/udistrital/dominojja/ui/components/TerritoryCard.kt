package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.Territory
import com.udistrital.dominojja.model.TerritoryEffect

private val Ink = Color(0xFF1B1B1B)

private data class Terrain(val emoji: String, val label: String, val color: Color)

private fun terrainOf(t: Territory): Terrain = when (t.effect) {
    TerritoryEffect.NONE -> Terrain("🌿", "Normal", Color(0xFFDCEDC8))
    TerritoryEffect.BONUS -> Terrain("⭐", "Bonus +${GameConfig.BONUS_POINTS}", Color(0xFFFFF59D))
    TerritoryEffect.TREASURE -> Terrain("💰", "Tesoro +${GameConfig.TREASURE_POINTS}", Color(0xFFFFE0B2))
    TerritoryEffect.TRAP -> Terrain("💣", "Trampa ${GameConfig.TRAP_POINTS}", Color(0xFFFFCDD2))
    TerritoryEffect.FORTRESS -> Terrain(
        "🏰", "Fortaleza ${t.conquests}/${GameConfig.FORTRESS_CONQUESTS}", Color(0xFFCFD8DC)
    )
    TerritoryEffect.PORTAL -> Terrain("🌀", "Portal → T${(t.portalTargetId ?: 0) + 1}", Color(0xFFB3E5FC))
}

@Composable
fun TerritoryCard(
    territory: Territory,
    ownerName: String?,
    ownerColor: Color?,
    canPlayLeft: Boolean,
    canPlayRight: Boolean,
    onPlay: (Side) -> Unit,
    modifier: Modifier = Modifier
) {
    val terrain = terrainOf(territory)
    val canPlay = canPlayLeft || canPlayRight
    val borderColor = when {
        ownerColor != null -> ownerColor
        canPlay -> Color(0xFF2E7D32)
        else -> Color(0xFF9E9E9E)
    }
    val buttonPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = terrain.color),
        border = BorderStroke(if (ownerColor != null || canPlay) 3.dp else 1.dp, borderColor)
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${terrain.emoji} T${territory.id + 1}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Ink
                )
                if (ownerColor != null) {
                    Text(
                        "👑 $ownerName",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier
                            .background(ownerColor, RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                } else {
                    Text("Libre", style = MaterialTheme.typography.labelSmall, color = Ink)
                }
            }
            Text(terrain.label, style = MaterialTheme.typography.labelMedium, color = Ink)

            // cadena de fichas colocadas
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 44.dp)
                    .background(Color(0x22000000), RoundedCornerShape(8.dp))
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (territory.tiles.isEmpty()) {
                    Text("Sin fichas", style = MaterialTheme.typography.labelSmall, color = Ink)
                } else {
                    territory.tiles.forEach { TileView(it, height = 34.dp) }
                }
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (territory.tiles.isEmpty()) {
                    Button(
                        onClick = { onPlay(Side.RIGHT) },
                        enabled = canPlayRight,
                        contentPadding = buttonPadding,
                        modifier = Modifier.weight(1f)
                    ) { Text("Colocar") }
                } else {
                    Button(
                        onClick = { onPlay(Side.LEFT) },
                        enabled = canPlayLeft,
                        contentPadding = buttonPadding,
                        modifier = Modifier.weight(1f)
                    ) { Text("◀ Izq") }
                    Button(
                        onClick = { onPlay(Side.RIGHT) },
                        enabled = canPlayRight,
                        contentPadding = buttonPadding,
                        modifier = Modifier.weight(1f)
                    ) { Text("Der ▶") }
                }
            }
        }
    }
}