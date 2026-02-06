package io.github.digitals01.battimuro.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import io.github.digitals01.battimuro.game.Difficulty
import io.github.digitals01.battimuro.game.GameLevel
import io.github.digitals01.battimuro.game.GameMode
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonGreen
import io.github.digitals01.battimuro.ui.theme.NeonMagenta

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun HomeScreen(
    onStartGame: (GameMode, Difficulty, Boolean, GameLevel) -> Unit,
    isLevelsPackOwned: Boolean = false,
    onOpenOptions: () -> Unit,
    onOpenShop: () -> Unit
) {
    val context = LocalContext.current
    val currentVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "v${packageInfo.versionName}"
        } catch (e: Exception) {
            "v1.0.3"
        }
    }

    var gameMode by remember { mutableStateOf(GameMode.ONE_VS_CPU) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }
    var playerIsLeft by remember { mutableStateOf(true) }
    var selectedLevel by remember { mutableStateOf(GameLevel.NONE) }
    var showAbout by remember { mutableStateOf(false) }

    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            title = { Text("Info su Battimuro") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text("Versione: $currentVersion\nSviluppatore: Louis Sanges\n\nIl classico gioco pong, reinventato per l'era moderna.\n\nNota: Questa versione \u00e8 distribuita gratuitamente perch\u00e9 \u00e8 in fase di test (anche se stabile), per capire se piace e se \u00e8 il caso di proseguire con lo sviluppo.")
                }
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

            Button(
                onClick = { onStartGame(gameMode, difficulty, playerIsLeft, selectedLevel) },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth(0.8f).height(56.dp)
            ) {
                Text("GIOCA", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = onOpenOptions) {
                    Text("Opzioni", color = Color.LightGray)
                }
                TextButton(onClick = onOpenShop) {
                    Text("Negozio", color = Color.LightGray)
                }
                TextButton(onClick = { showAbout = true }) {
                    Text("Info", color = Color.LightGray)
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

            if (gameMode == GameMode.ONE_VS_CPU) {
                Text("DIFFICOLTÀ", color = Color.Gray, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { diff ->
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

            Spacer(modifier = Modifier.height(16.dp))

            Text("LIVELLO", color = Color.Gray, fontSize = 12.sp)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                GameLevel.entries.forEach { level ->
                    val enabled = level == GameLevel.NONE || isLevelsPackOwned
                    FilterChip(
                        selected = selectedLevel == level,
                        onClick = { if (enabled) selectedLevel = level },
                        enabled = enabled,
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (level != GameLevel.NONE && !isLevelsPackOwned) {
                                    Icon(
                                        Icons.Filled.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                }
                                Text(level.displayName)
                            }
                        }
                    )
                }
            }
        }
    }
}
