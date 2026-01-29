package com.example.battimuro.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battimuro.game.Difficulty
import com.example.battimuro.game.GameMode
import com.example.battimuro.ui.theme.NeonCyan
import com.example.battimuro.ui.theme.NeonGreen
import com.example.battimuro.ui.theme.NeonMagenta

@Composable
fun HomeScreen(
    onStartGame: (GameMode, Difficulty, Boolean) -> Unit
) {
    var gameMode by remember { mutableStateOf(GameMode.ONE_VS_CPU) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var playerIsLeft by remember { mutableStateOf(true) }
    var showAbout by remember { mutableStateOf(false) }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("Info su Battimuro") },
            text = { 
                Text("Versione: 0.9 stable\nSviluppatore: Louis Sanges\n\nIl classico gioco pong, reinventato per l'era moderna.") 
            },
            confirmButton = {
                TextButton(onClick = { showAbout = false }) {
                    Text("Chiudi")
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LEFT COLUMN: Branding & Main Action
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "BATTIMURO",
                style = TextStyle(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    shadow = Shadow(color = NeonMagenta, blurRadius = 10f)
                )
            )
            Text(
                text = "NEON LANDSCAPE",
                style = TextStyle(
                    fontSize = 20.sp,
                    color = NeonGreen,
                    shadow = Shadow(color = NeonCyan, blurRadius = 5f)
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Start Button
            Button(
                onClick = { onStartGame(gameMode, difficulty, playerIsLeft) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("GIOCA", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer: About & Update
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = { showAbout = true }) {
                    Text("Info", color = Color.LightGray)
                }
                TextButton(onClick = { /* Placeholder */ }) {
                    Text("Aggiornamenti", color = Color.LightGray)
                }
            }
        }

        // Vertical Divider
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.8f)
                .background(Color.DarkGray)
        )

        // RIGHT COLUMN: Options
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
             // Game Mode
            Text("MODALITÀ", color = Color.Gray, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = gameMode == GameMode.ONE_VS_CPU,
                    onClick = { gameMode = GameMode.ONE_VS_CPU },
                    label = { Text("1 VS CPU") }
                )
                FilterChip(
                    selected = gameMode == GameMode.ONE_VS_ONE,
                    onClick = { gameMode = GameMode.ONE_VS_ONE },
                    label = { Text("1 VS 1") }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Difficulty (Only if CPU)
            if (gameMode == GameMode.ONE_VS_CPU) {
                Text("DIFFICOLTÀ", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.values().forEach { diff ->
                        val label = when(diff) {
                            Difficulty.EASY -> "FACILE"
                            Difficulty.MEDIUM -> "MEDIO"
                            Difficulty.HARD -> "DIFFICILE"
                        }
                        FilterChip(
                            selected = difficulty == diff,
                            onClick = { difficulty = diff },
                            label = { Text(label) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Zone Selection (Only if CPU)
            Text(if(gameMode == GameMode.ONE_VS_CPU) "LA TUA ZONA" else "ZONA G1 (SX)", color = Color.Gray, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = playerIsLeft,
                    onClick = { playerIsLeft = true },
                    label = { Text("SINISTRA") }
                )
                FilterChip(
                    selected = !playerIsLeft,
                    onClick = { playerIsLeft = false },
                    label = { Text("DESTRA") }
                )
            }
        }
    }
}

