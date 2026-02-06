package io.github.digitals01.battimuro.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonMagenta

@Composable
fun OptionsScreen(
    ballStyle: BallStyle,
    paddleStyle: PaddleStyle,
    onBallStyleChange: (BallStyle) -> Unit,
    onPaddleStyleChange: (PaddleStyle) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "OPZIONI",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = NeonCyan,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Text("STILE PALLINA", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BallStyle.entries.forEach { style ->
                val label = when (style) {
                    BallStyle.NEON -> "NEON"
                    BallStyle.MINIMAL -> "MINIMAL"
                    BallStyle.FLAME -> "FIAMMA"
                }
                FilterChip(
                    selected = ballStyle == style,
                    onClick = { onBallStyleChange(style) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("STILE RESPINGENTI", color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PaddleStyle.entries.forEach { style ->
                val label = when (style) {
                    PaddleStyle.NEON -> "NEON"
                    PaddleStyle.MINIMAL -> "MINIMAL"
                    PaddleStyle.SOLID -> "SOLIDO"
                }
                FilterChip(
                    selected = paddleStyle == style,
                    onClick = { onPaddleStyleChange(style) },
                    label = { Text(label) }
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        OutlinedButton(
            onClick = onBack,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonMagenta)
        ) {
            Text("INDIETRO", fontWeight = FontWeight.Bold)
        }
    }
}
