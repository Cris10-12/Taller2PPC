package com.udistrital.dominojja.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.model.Tile

@Composable
fun TileView(
    tile: Tile,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(6.dp)
    val borderColor =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, shape)
            .border(if (selected) 3.dp else 1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${tile.left}", fontWeight = FontWeight.Bold)
        Text(" | ", color = MaterialTheme.colorScheme.outline)
        Text("${tile.right}", fontWeight = FontWeight.Bold)
    }
}