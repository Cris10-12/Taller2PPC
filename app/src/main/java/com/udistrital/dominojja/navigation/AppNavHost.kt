package com.udistrital.dominojja.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.udistrital.dominojja.common.GameConfig
import com.udistrital.dominojja.data.AuthRepository
import com.udistrital.dominojja.ui.screens.CreateRoomScreen
import com.udistrital.dominojja.ui.screens.GameScreen
import com.udistrital.dominojja.ui.screens.HomeScreen
import com.udistrital.dominojja.ui.screens.HowToPlayScreen
import com.udistrital.dominojja.ui.screens.JoinRoomScreen
import com.udistrital.dominojja.ui.screens.LocalSetupScreen
import com.udistrital.dominojja.ui.screens.SettingsScreen
import com.udistrital.dominojja.ui.screens.WaitingRoomScreen
import com.udistrital.dominojja.viewModel.GameViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue



@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val auth = remember { AuthRepository() }
            var userName by remember { mutableStateOf(auth.displayName) }

            HomeScreen(
                onPlay = { navController.navigate(Routes.LOCAL) },
                onCreateRoom = { navController.navigate(Routes.CREATE_ROOM) },
                onJoinRoom = { navController.navigate(Routes.JOIN_ROOM) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onHowToPlay = { navController.navigate(Routes.HOW_TO_PLAY) },
                userName = userName,
                onGoogleSignIn = {
                    scope.launch {
                        runCatching { auth.signInWithGoogle(context) }
                            .onSuccess { userName = auth.displayName }
                            .onFailure { Log.e("GoogleLogin", "falló", it) }
                    }
                }
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
                onRoomCreated = { code ->
                    navController.navigate(Routes.waitingRoom(code)) {
                        popUpTo(Routes.CREATE_ROOM) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.JOIN_ROOM) {
            JoinRoomScreen(
                onBack = { navController.popBackStack() },
                onJoined = { code ->
                    navController.navigate(Routes.waitingRoom(code)) {
                        popUpTo(Routes.JOIN_ROOM) { inclusive = true }
                    }
                }
            )
        }
        composable(
            Routes.WAITING_ROOM,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { entry ->
            val code = entry.arguments?.getString("code") ?: ""
            WaitingRoomScreen(
                code = code,
                onBack = { navController.popBackStack(Routes.HOME, inclusive = false) },
                onGameStarted = {
                    navController.navigate(Routes.gameOnline(code)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }
        composable(
            Routes.GAME_ONLINE,
            arguments = listOf(navArgument("code") { type = NavType.StringType })
        ) { entry ->
            val code = entry.arguments?.getString("code") ?: ""
            val vm: GameViewModel = viewModel()
            LaunchedEffect(code) { vm.startOnline(code) }
            GameScreen(
                viewModel = vm,
                onExit = {
                    vm.stopOnline()
                    navController.popBackStack(Routes.HOME, false)
                }
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