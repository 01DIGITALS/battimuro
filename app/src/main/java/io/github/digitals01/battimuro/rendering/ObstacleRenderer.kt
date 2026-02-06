package io.github.digitals01.battimuro.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import io.github.digitals01.battimuro.game.Obstacle
import io.github.digitals01.battimuro.ui.theme.NeonCyan
import io.github.digitals01.battimuro.ui.theme.NeonMagenta

fun DrawScope.drawObstacles(
    obstacles: List<Obstacle>,
    gameTimeMs: Long
) {
    obstacles.forEach { obstacle ->
        // Outer glow
        drawRect(
            color = NeonCyan.copy(alpha = 0.15f),
            topLeft = Offset(obstacle.position.x - 3f, obstacle.position.y - 3f),
            size = Size(obstacle.size.width + 6f, obstacle.size.height + 6f)
        )

        // Main body with gradient
        drawRect(
            brush = Brush.verticalGradient(
                listOf(NeonCyan.copy(alpha = 0.7f), NeonMagenta.copy(alpha = 0.7f))
            ),
            topLeft = obstacle.position,
            size = obstacle.size
        )

        // Border
        drawRect(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = obstacle.position,
            size = obstacle.size,
            style = Stroke(width = 1.5f)
        )
    }
}
