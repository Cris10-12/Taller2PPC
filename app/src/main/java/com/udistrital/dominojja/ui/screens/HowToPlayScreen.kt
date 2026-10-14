package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.udistrital.dominojja.ui.components.AppScaffold

@Composable
fun HowToPlayScreen(onBack: () -> Unit) {
    AppScaffold(title = "Cómo jugar", onBack = onBack) {
        Text(
            text = "Cada jugador recibe 7 fichas. El resto forma el pozo.\n\n" +
                    "El tablero está dividido en territorios. Al colocar una ficha conquistas el territorio.\n\n" +
                    "Efectos:\n" +
                    "Bonus: puntos extra.\n" +
                    "Tesoro: +5 puntos.\n" +
                    "Trampa: -2 puntos.\n" +
                    "Fortaleza: requiere dos conquistas.\n" +
                    "Portal: te mueve a otro territorio.\n\n" +
                    "Gana quien tenga más puntos al terminar. En empate, quien tenga más territorios.",
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}