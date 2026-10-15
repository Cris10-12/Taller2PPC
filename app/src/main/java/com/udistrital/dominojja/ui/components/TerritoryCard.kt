package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.logic.Side
import com.udistrital.dominojja.model.Territory
import com.udistrital.dominojja.model.TerritoryEffect

private fun effectLabel(t: Territory): String = when (t.effect) {
    TerritoryEffect.NONE -> "Normal"
    TerritoryEffect.BONUS -> "Bonus +${GameConfig.BONUS_POINTS}"
    TerritoryEffect.TREASURE -> "Tesoro +${GameConfig.TREASURE_POINTS}"
    TerritoryEffect.TRAP -> "Trampa ${GameConfig.TRAP_POINTS}"
    TerritoryEffect.FORTRESS -> "Fortaleza ${t.conquests}/${GameConfig.FORTRESS_CONQUESTS}"
    TerritoryEffect.PORTAL -> "Portal → ${(t.portalTargetId ?: 0) + 1}"
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
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ownerColor?.copy(alpha = 0.25f)
                ?: MaterialTheme.colorScheme.surfaceVariant
        ),
        border = ownerColor?.let { BorderStroke(2.dp, it) }
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "Territorio ${territory.id + 1} · ${effectLabel(territory)}",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(ownerName ?: "Libre")
            }
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (territory.tiles.isEmpty()) Text("Sin fichas")
                else territory.tiles.forEach { TileView(it) }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (territory.tiles.isEmpty()) {
                    OutlinedButton(onClick = { onPlay(Side.RIGHT) }, enabled = canPlayRight) {
                        Text("Colocar")
                    }
                } else {
                    OutlinedButton(onClick = { onPlay(Side.LEFT) }, enabled = canPlayLeft) {
                        Text("← Izq")
                    }
                    OutlinedButton(onClick = { onPlay(Side.RIGHT) }, enabled = canPlayRight) {
                        Text("Der →")
                    }
                }
            }
        }
    }
}