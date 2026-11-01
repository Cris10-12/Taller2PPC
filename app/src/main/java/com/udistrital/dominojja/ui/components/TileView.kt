package com.udistrital.dominojja.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.model.Tile

private val Ivory = Color(0xFFFFF8E7)
private val Edge = Color(0xFF6D5B3A)
private val Pip = Color(0xFF1F1F1F)

@Composable
fun TileView(
    tile: Tile,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    dimmed: Boolean = false,
    height: Dp = 44.dp,
    onClick: (() -> Unit)? = null
) {
    val shape = RoundedCornerShape(height * 0.18f)
    val lift by animateDpAsState(if (selected) (-8).dp else 0.dp, label = "lift")

    Box(
        modifier
            .offset(y = lift)
            .size(width = height * 2, height = height)
            .alpha(if (dimmed) 0.4f else 1f)
            .shadow(if (selected) 8.dp else 2.dp, shape)
            .clip(shape)
            .background(Ivory)
            .border(
                if (selected) 3.dp else 1.dp,
                if (selected) Color(0xFFF9A825) else Edge,
                shape
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val half = size.width / 2f
            drawLine(
                Edge,
                Offset(half, size.height * 0.12f),
                Offset(half, size.height * 0.88f),
                strokeWidth = 2.dp.toPx()
            )
            drawPips(tile.left, 0f, half, size.height)
            drawPips(tile.right, half, half, size.height)
        }
    }
}

private fun DrawScope.drawPips(value: Int, x0: Float, w: Float, h: Float) {
    val r = minOf(w, h) * 0.085f
    pipLayout(value).forEach { (px, py) ->
        drawCircle(Pip, r, Offset(x0 + px * w, py * h))
    }
}

private fun pipLayout(v: Int): List<Pair<Float, Float>> {
    val a = 0.27f
    val b = 0.73f
    val c = 0.5f
    return when (v) {
        0 -> emptyList()
        1 -> listOf(c to c)
        2 -> listOf(a to a, b to b)
        3 -> listOf(a to a, c to c, b to b)
        4 -> listOf(a to a, b to a, a to b, b to b)
        5 -> listOf(a to a, b to a, c to c, a to b, b to b)
        else -> listOf(a to a, b to a, a to c, b to c, a to b, b to b)
    }
}