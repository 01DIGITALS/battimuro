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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.drawscope.Stroke
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
import io.github.digitals01.battimuro.ui.theme.DarkBackground
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonGreen
import io.github.digitals01.battimuro.ui.theme.NeonMagenta
import io.github.digitals01.battimuro.ui.theme.NeonOrange
import io.github.digitals01.battimuro.ui.theme.NeonRed

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

            // Ball rendering
            when (ballStyle) {
                BallStyle.NEON -> {
                    drawCircle(
                        color = NeonGreen.copy(alpha = 0.3f),
                        radius = gameEngine.ball.radius * 2,
                        center = gameEngine.ball.position
                    )
                    drawCircle(
                        color = NeonGreen,
                        radius = gameEngine.ball.radius,
                        center = gameEngine.ball.position
                    )
                }
                BallStyle.MINIMAL -> {
                    drawCircle(
                        color = Color.White,
                        radius = gameEngine.ball.radius,
                        center = gameEngine.ball.position,
                        style = Stroke(width = 2f)
                    )
                }
                BallStyle.FLAME -> {
                    val trail = gameEngine.ballTrail
                    trail.forEachIndexed { index, pos ->
                        val progress = (index + 1).toFloat() / (trail.size + 1)
                        val trailRadius = gameEngine.ball.radius * progress * 0.8f
                        val trailColor = androidx.compose.ui.graphics.lerp(NeonRed, NeonOrange, progress)
                        drawCircle(
                            color = trailColor.copy(alpha = progress * 0.5f),
                            radius = trailRadius,
                            center = pos
                        )
                    }
                    drawCircle(
                        color = NeonOrange.copy(alpha = 0.4f),
                        radius = gameEngine.ball.radius * 1.8f,
                        center = gameEngine.ball.position
                    )
                    drawCircle(
                        color = NeonGreen,
                        radius = gameEngine.ball.radius,
                        center = gameEngine.ball.position
                    )
                }
            }

            // Paddle rendering
            when (paddleStyle) {
                PaddleStyle.NEON -> {
                    drawRect(
                        brush = Brush.verticalGradient(listOf(NeonCyan, NeonMagenta)),
                        topLeft = gameEngine.leftPaddle.position,
                        size = gameEngine.leftPaddle.size
                    )
                    drawRect(
                        brush = Brush.verticalGradient(listOf(NeonMagenta, NeonCyan)),
                        topLeft = gameEngine.rightPaddle.position,
                        size = gameEngine.rightPaddle.size
                    )
                }
                PaddleStyle.MINIMAL -> {
                    drawRect(
                        color = Color.White,
                        topLeft = gameEngine.leftPaddle.position,
                        size = gameEngine.leftPaddle.size,
                        style = Stroke(width = 2f)
                    )
                    drawRect(
                        color = Color.White,
                        topLeft = gameEngine.rightPaddle.position,
                        size = gameEngine.rightPaddle.size,
                        style = Stroke(width = 2f)
                    )
                }
                PaddleStyle.SOLID -> {
                    drawRect(
                        color = NeonCyan,
                        topLeft = gameEngine.leftPaddle.position,
                        size = gameEngine.leftPaddle.size
                    )
                    drawRect(
                        color = NeonMagenta,
                        topLeft = gameEngine.rightPaddle.position,
                        size = gameEngine.rightPaddle.size
                    )
                }
            }
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
