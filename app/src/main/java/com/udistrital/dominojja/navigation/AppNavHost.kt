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
import  androidx.compose.runtime.LaunchedEffect
import  androidx.lifecycle.viewmodel.compose.viewModel
import  androidx.navigation.NavType
import androidx.navigation.navArgument
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.ui.screens.GameScreen
import com.udistrital.dominojja.viewModel.GameViewModel

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
                onStart = { n -> navController.navigate(Routes.game(n)) }
            )
        }
        composable(
            Routes.GAME, arguments = listOf(navArgument("players") { type = NavType.IntType })
        ) { entry ->
            val players = entry.arguments?.getInt("players") ?: GameConfig.DEFAULT_PLAYERS
            val vm: GameViewModel = viewModel()
            LaunchedEffect(Unit) { vm.startLocal(players) }
            GameScreen(viewModel = vm, onExit = { navController.popBackStack(Routes.HOME, false) })
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