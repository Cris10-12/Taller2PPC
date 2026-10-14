package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.common.GameConfig

@Composable
fun PlayerCountSelector(selected: Int, onSelect: (Int) -> Unit) {
    Column {
        Text("Jugadores")
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            GameConfig.PLAYER_OPTIONS.forEach { n ->
                FilterChip(
                    selected = selected == n,
                    onClick = { onSelect(n) },
                    label = { Text("$n") }
                )
            }
        }
    }
}