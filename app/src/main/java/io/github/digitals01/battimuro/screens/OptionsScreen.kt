package io.github.digitals01.battimuro.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.game.GameLevel
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonMagenta

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OptionsScreen(
    ballStyle: BallStyle,
    paddleStyle: PaddleStyle,
    gameLevel: GameLevel,
    onBallStyleChange: (BallStyle) -> Unit,
    onPaddleStyleChange: (PaddleStyle) -> Unit,
    onGameLevelChange: (GameLevel) -> Unit,
    isStylePackOwned: Boolean,
    isLevelsPackOwned: Boolean,
    isBonusPackOwned: Boolean,
    powerUpsEnabled: Boolean,
    onPowerUpsToggle: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "OPZIONI",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Text("STILE PALLINA", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BallStyle.entries.forEach { style ->
                val label = ballStyleLabel(style)
                val enabled = !style.isPremium || isStylePackOwned
                FilterChip(
                    selected = ballStyle == style,
                    onClick = { if (enabled) onBallStyleChange(style) },
                    enabled = enabled,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (style.isPremium && !isStylePackOwned) {
                                Icon(
                                    Icons.Filled.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(label)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("STILE RESPINGENTI", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PaddleStyle.entries.forEach { style ->
                val label = paddleStyleLabel(style)
                val enabled = !style.isPremium || isStylePackOwned
                FilterChip(
                    selected = paddleStyle == style,
                    onClick = { if (enabled) onPaddleStyleChange(style) },
                    enabled = enabled,
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (style.isPremium && !isStylePackOwned) {
                                Icon(
                                    Icons.Filled.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                            }
                            Text(label)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("LIVELLO", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GameLevel.entries.forEach { level ->
                val enabled = level == GameLevel.NONE || isLevelsPackOwned
                FilterChip(
                    selected = gameLevel == level,
                    onClick = { if (enabled) onGameLevelChange(level) },
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

        if (isBonusPackOwned) {
            Spacer(modifier = Modifier.height(20.dp))

            Text("POWER-UP", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (powerUpsEnabled) "ATTIVI" else "DISATTIVI",
                    color = if (powerUpsEnabled) NeonMagenta else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Switch(
                    checked = powerUpsEnabled,
                    onCheckedChange = onPowerUpsToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonMagenta,
                        checkedTrackColor = NeonMagenta.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedButton(
            onClick = onBack,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta)
        ) {
            Text("INDIETRO", fontWeight = FontWeight.Bold)
        }
    }
}

private fun ballStyleLabel(style: BallStyle): String = when (style) {
    BallStyle.NEON -> "NEON"
    BallStyle.MINIMAL -> "MINIMAL"
    BallStyle.FLAME -> "FIAMMA"
    BallStyle.PULSE -> "PULSANTE"
    BallStyle.PRISMA -> "PRISMA"
    BallStyle.GHOST -> "FANTASMA"
    BallStyle.PIXEL -> "PIXEL"
    BallStyle.PLASMA -> "PLASMA"
    BallStyle.ICE -> "GHIACCIO"
    BallStyle.GOLD -> "ORO"
}

private fun paddleStyleLabel(style: PaddleStyle): String = when (style) {
    PaddleStyle.NEON -> "NEON"
    PaddleStyle.MINIMAL -> "MINIMAL"
    PaddleStyle.SOLID -> "SOLIDO"
    PaddleStyle.PULSE -> "PULSANTE"
    PaddleStyle.PRISMA -> "PRISMA"
    PaddleStyle.GHOST -> "FANTASMA"
    PaddleStyle.PIXEL -> "PIXEL"
    PaddleStyle.PLASMA -> "PLASMA"
    PaddleStyle.ICE -> "GHIACCIO"
    PaddleStyle.GOLD -> "ORO"
}
