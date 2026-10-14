package com.udistrital.dominojja.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.udistrital.dominojja.ui.components.AppScaffold
import com.udistrital.dominojja.common.Spacing

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var sound by remember { mutableStateOf(true) }
    var vibration by remember { mutableStateOf(true) }

    AppScaffold(title = "Configuración", onBack = onBack) {
        SettingSwitch("Sonido", sound) { sound = it }
        Spacer(Modifier.height(Spacing.medium))
        SettingSwitch("Vibración", vibration) { vibration = it }
    }
}