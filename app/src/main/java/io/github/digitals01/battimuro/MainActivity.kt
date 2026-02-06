package io.github.digitals01.battimuro

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.screens.GameScreen
import io.github.digitals01.battimuro.screens.HomeScreen
import io.github.digitals01.battimuro.screens.OptionsScreen
import io.github.digitals01.battimuro.ui.theme.BattimuroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lock orientation to Landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Delay splash screen for 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            setTheme(R.style.Theme_Battimuro)

            setContent {
                BattimuroTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        BattimuroApp()
                    }
                }
            }
        }, 2000)
    }
}

enum class Screen {
    HOME,
    OPTIONS,
    GAME
}

@Composable
fun BattimuroApp() {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    // Game Options State
    var gameMode by remember { mutableStateOf(io.github.digitals01.battimuro.game.GameMode.ONE_VS_CPU) }
    var difficulty by remember { mutableStateOf(io.github.digitals01.battimuro.game.Difficulty.MEDIUM) }
    var playerIsLeft by remember { mutableStateOf(true) }
    var ballStyle by remember { mutableStateOf(BallStyle.NEON) }
    var paddleStyle by remember { mutableStateOf(PaddleStyle.NEON) }

    when (currentScreen) {
        Screen.HOME -> {
            HomeScreen(
                onStartGame = { mode, diff, isLeft ->
                    gameMode = mode
                    difficulty = diff
                    playerIsLeft = isLeft
                    currentScreen = Screen.GAME
                },
                onOpenOptions = {
                    currentScreen = Screen.OPTIONS
                }
            )
        }
        Screen.OPTIONS -> {
            OptionsScreen(
                ballStyle = ballStyle,
                paddleStyle = paddleStyle,
                onBallStyleChange = { ballStyle = it },
                onPaddleStyleChange = { paddleStyle = it },
                onBack = { currentScreen = Screen.HOME }
            )
        }
        Screen.GAME -> {
            GameScreen(
                gameMode = gameMode,
                difficulty = difficulty,
                playerIsLeft = playerIsLeft,
                ballStyle = ballStyle,
                paddleStyle = paddleStyle,
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
