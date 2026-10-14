package com.udistrital.dominojja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.udistrital.dominojja.navigation.AppNavHost
import com.udistrital.dominojja.ui.theme.DominoJjaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DominoJjaTheme() {
                AppNavHost()
            }
        }
    }
}