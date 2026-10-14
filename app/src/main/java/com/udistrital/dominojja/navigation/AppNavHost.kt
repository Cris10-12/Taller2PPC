package com.udistrital.dominojja.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.udistrital.dominojja.ui.screens.CreateRoomScreen
import com.udistrital.dominojja.ui.screens.HomeScreen
import com.udistrital.dominojja.ui.screens.HowToPlayScreen
import com.udistrital.dominojja.ui.screens.JoinRoomScreen
import com.udistrital.dominojja.ui.screens.LocalSetupScreen
import com.udistrital.dominojja.ui.screens.SettingsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPlay = { navController.navigate(Routes.LOCAL) },
                onCreateRoom = { navController.navigate(Routes.CREATE_ROOM) },
                onJoinRoom = { navController.navigate(Routes.JOIN_ROOM) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onHowToPlay = { navController.navigate(Routes.HOW_TO_PLAY) }
            )
        }
        composable(Routes.LOCAL) {
            LocalSetupScreen(
                onBack = { navController.popBackStack() },
                onStart = { }
            )
        }
        composable(Routes.CREATE_ROOM) {
            CreateRoomScreen(
                onBack = { navController.popBackStack() },
                onCreate = { _, _ -> }
            )
        }
        composable(Routes.JOIN_ROOM) {
            JoinRoomScreen(
                onBack = { navController.popBackStack() },
                onJoin = { _, _ -> }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.HOW_TO_PLAY) {
            HowToPlayScreen(onBack = { navController.popBackStack() })
        }
    }
}