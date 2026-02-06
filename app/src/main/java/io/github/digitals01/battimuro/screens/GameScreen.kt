package io.github.digitals01.battimuro.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.game.Difficulty
import io.github.digitals01.battimuro.game.GameEngine
import io.github.digitals01.battimuro.game.GameMode
import io.github.digitals01.battimuro.game.GameStatus
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.rendering.drawBall
import io.github.digitals01.battimuro.rendering.drawPaddle
import io.github.digitals01.battimuro.ui.theme.DarkBackground
import io.github.digitals01.battimuro.ui.theme.NeonGreen

@Composable
fun GameScreen(
    gameMode: GameMode,
    difficulty: Difficulty,
    playerIsLeft: Boolean,
    ballStyle: BallStyle,
    paddleStyle: PaddleStyle,
    onGameOver: () -> Unit,
    onBack: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val safeZoneWidth = 40.dp

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Left safe zone
        Box(
            modifier = Modifier
                .width(safeZoneWidth)
                .fillMaxHeight()
        )

    BoxWithConstraints(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val width = maxWidth.value * density
        val height = maxHeight.value * density

        val gameEngine = remember(width, height) {
            GameEngine(width, height, gameMode, difficulty, playerIsLeft).apply { start() }
        }

        LaunchedEffect(Unit) {
            var lastFrameTime = 0L
            while (true) {
                withFrameNanos { frameTimeNanos ->
                    if (lastFrameTime != 0L) {
                        val deltaMs = (frameTimeNanos - lastFrameTime) / 1_000_000
                        gameEngine.update(deltaMs)
                    }
                    lastFrameTime = frameTimeNanos
                }
            }
        }

        LaunchedEffect(showMenu) {
            if (showMenu) gameEngine.pause() else gameEngine.resume()
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {}
        ) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )

            drawBall(
                ball = gameEngine.ball,
                style = ballStyle,
                trail = gameEngine.ballTrail,
                gameTimeMs = gameEngine.gameTimeMs
            )

            drawPaddle(
                leftPaddle = gameEngine.leftPaddle,
                rightPaddle = gameEngine.rightPaddle,
                style = paddleStyle,
                gameTimeMs = gameEngine.gameTimeMs
            )
        }

        Row(Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            if (gameMode == GameMode.ONE_VS_CPU && playerIsLeft) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = true)
                            } else if (gameMode == GameMode.ONE_VS_ONE) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = true)
                            }
                        }
                    }
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            if (gameMode == GameMode.ONE_VS_CPU && !playerIsLeft) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = false)
                            } else if (gameMode == GameMode.ONE_VS_ONE) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = false)
                            }
                        }
                    }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "${gameEngine.playerScore}",
                color = if (playerIsLeft) NeonGreen else Color.White.copy(alpha=0.5f),
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }

            Text(
                text = "${gameEngine.cpuScore}",
                color = if (!playerIsLeft) NeonGreen else Color.White.copy(alpha=0.5f),
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }

        if (showMenu) {
            AlertDialog(
                onDismissRequest = { showMenu = false },
                title = { Text("Gioco in Pausa") },
                text = { Text("Vuoi tornare al menu principale?") },
                confirmButton = {
                    TextButton(onClick = onBack) {
                        Text("Esci", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showMenu = false }) {
                        Text("Riprendi")
                    }
                }
            )
        }
    } // BoxWithConstraints

        // Right safe zone
        Box(
            modifier = Modifier
                .width(safeZoneWidth)
                .fillMaxHeight()
        )
    } // Row
}
