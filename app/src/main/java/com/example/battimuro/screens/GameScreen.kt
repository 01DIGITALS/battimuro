package com.example.battimuro.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import com.example.battimuro.game.Difficulty
import com.example.battimuro.game.GameEngine
import com.example.battimuro.game.GameMode
import com.example.battimuro.game.GameStatus
import com.example.battimuro.ui.theme.DarkBackground
import com.example.battimuro.ui.theme.NeonCyan
import com.example.battimuro.ui.theme.NeonGreen
import com.example.battimuro.ui.theme.NeonMagenta

@Composable
fun GameScreen(
    gameMode: GameMode,
    difficulty: Difficulty,
    playerIsLeft: Boolean,
    onGameOver: () -> Unit,
    onBack: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val width = maxWidth.value * density
        val height = maxHeight.value * density
        
        // Initialize Game Engine
        val gameEngine = remember(width, height) {
            GameEngine(width, height, gameMode, difficulty, playerIsLeft).apply { start() }
        }

        // Game Loop
        LaunchedEffect(Unit) {
            while (true) {
                withFrameNanos { 
                    gameEngine.update(16) 
                }
            }
        }
        
        // Pause/Resume Logic
        LaunchedEffect(showMenu) {
            if (showMenu) gameEngine.pause() else gameEngine.resume()
        }

        // Draw Game
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // Primitive Multi-touch handling: simpler to just use two Box areas if possible for 1v1
                    // For now, let's use a global custom gesture detector or overlay if we can.
                    // To keep it robust within Compose without external libs, let's use a Split Layout for 1v1 inputs
                    // But we want to draw on one canvas.
                    // Solution: This Canvas handles ONE gesture (or global).
                    // Better approach: Transparent boxes on top for controls.
                }
        ) {
            // Force redraw on state changes (though state objects trigger it automatically usually)
            // Accessing state variables inside draw scope registers them as dependencies
            
            // Divider
            drawLine(
                color = Color.DarkGray,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 2f,
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(20f, 20f))
            )

            // Ball
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

            // Left Paddle
            drawRect(
                brush = Brush.verticalGradient(listOf(NeonCyan, NeonMagenta)),
                topLeft = gameEngine.leftPaddle.position,
                size = gameEngine.leftPaddle.size
            )
            
            // Right Paddle
            drawRect(
                brush = Brush.verticalGradient(listOf(NeonMagenta, NeonCyan)),
                topLeft = gameEngine.rightPaddle.position,
                size = gameEngine.rightPaddle.size
            )
        }
        
        // Input Overlays (Transparent) - Splitting screen effectively for inputs
        Row(Modifier.fillMaxSize()) {
            // Left Zone Input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { change, trash ->
                            // If 1vCPU and player is left: control left
                            if (gameMode == GameMode.ONE_VS_CPU && playerIsLeft) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = true)
                            } else if (gameMode == GameMode.ONE_VS_ONE) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = true)
                            }
                        }
                    }
            )
            
            // Right Zone Input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .pointerInput(Unit) {
                        detectDragGestures { change, trash ->
                             // If 1vCPU and player is right: control right
                            if (gameMode == GameMode.ONE_VS_CPU && !playerIsLeft) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = false)
                            } else if (gameMode == GameMode.ONE_VS_ONE) {
                                gameEngine.updatePaddle(change.position.y, isLeftPaddle = false)
                            }
                        }
                    }
            )
        }

        // Score Overlay (Top Center)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "${gameEngine.playerScore}", // Technically Left Score
                color = if (playerIsLeft) NeonGreen else Color.White.copy(alpha=0.5f),
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            // Menu Button (Discreet)
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = Color.White.copy(alpha = 0.5f)
                )
            }
            
            Text(
                text = "${gameEngine.cpuScore}", // Technically Right Score
                color = if (!playerIsLeft) NeonGreen else Color.White.copy(alpha=0.5f),
                fontSize = 48.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
        
        // Pause Menu Dialog
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
    }
}
