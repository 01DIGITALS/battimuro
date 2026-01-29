package com.example.battimuro

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.battimuro.screens.GameScreen
import com.example.battimuro.screens.HomeScreen
import com.example.battimuro.ui.theme.BattimuroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle Splash Screen transition
        Thread.sleep(2000) // Keep splash screen for 2 seconds
        setTheme(R.style.Theme_Battimuro)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Lock orientation to Landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        setContent {
            BattimuroTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BattimuroApp()
                }
            }
        }
    }
}

enum class Screen {
    HOME,
    GAME
}

@Composable
fun BattimuroApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    
    // Game Options State
    var gameMode by remember { mutableStateOf(com.example.battimuro.game.GameMode.ONE_VS_CPU) }
    var difficulty by remember { mutableStateOf(com.example.battimuro.game.Difficulty.MEDIUM) }
    var playerIsLeft by remember { mutableStateOf(true) } // Renamed from playerIsBottom

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onStartGame = { mode, diff, isLeft ->
                    gameMode = mode
                    difficulty = diff
                    playerIsLeft = isLeft
                    currentScreen = Screen.GAME
                }
            )
        }
        Screen.GAME -> {
            GameScreen(
                gameMode = gameMode,
                difficulty = difficulty,
                playerIsLeft = playerIsLeft,
                onGameOver = { 
                    currentScreen = Screen.HOME
                },
                onBack = {
                    currentScreen = Screen.HOME
                }
            )
        }
    }
}