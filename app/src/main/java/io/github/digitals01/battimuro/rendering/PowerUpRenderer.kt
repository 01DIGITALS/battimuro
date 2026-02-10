package io.github.digitals01.battimuro.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.github.digitals01.battimuro.game.ActiveEffect
import io.github.digitals01.battimuro.game.Paddle
import io.github.digitals01.battimuro.game.PowerUp
import io.github.digitals01.battimuro.game.PowerUpType
import io.github.digitals01.battimuro.ui.theme.PowerUpGreen
import io.github.digitals01.battimuro.ui.theme.PowerUpRed
import io.github.digitals01.battimuro.ui.theme.PowerUpYellow
import kotlin.math.sin

fun DrawScope.drawPowerUp(powerUp: PowerUp, gameTimeMs: Long, textMeasurer: TextMeasurer) {
    val pulse = (sin(gameTimeMs * 0.005f) * 0.3f + 1f).toFloat()
    val color = when (powerUp.type) {
        PowerUpType.WIDER_PADDLE -> PowerUpGreen
        PowerUpType.MULTIBALL -> PowerUpYellow
        PowerUpType.PADDLE_SHRINK -> PowerUpRed
    }

    // Outer glow
    drawCircle(
        color = color.copy(alpha = 0.15f * pulse),
        radius = powerUp.radius * 2.5f * pulse,
        center = powerUp.position
    )

    // Mid glow
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = powerUp.radius * 1.5f,
        center = powerUp.position
    )

    // Core circle
    drawCircle(
        color = color,
        radius = powerUp.radius,
        center = powerUp.position
    )

    // Symbol
    when (powerUp.type) {
        PowerUpType.WIDER_PADDLE -> {
            val symbol = textMeasurer.measure(
                "+",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
            drawText(
                symbol,
                topLeft = Offset(
                    powerUp.position.x - symbol.size.width / 2,
                    powerUp.position.y - symbol.size.height / 2
                )
            )
        }
        PowerUpType.PADDLE_SHRINK -> {
            val symbol = textMeasurer.measure(
                "−",
                style = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
            drawText(
                symbol,
                topLeft = Offset(
                    powerUp.position.x - symbol.size.width / 2,
                    powerUp.position.y - symbol.size.height / 2
                )
            )
        }
        PowerUpType.MULTIBALL -> {
            val r = powerUp.radius * 0.25f
            drawCircle(color = Color.Black, radius = r, center = powerUp.position + Offset(-r * 1.5f, 0f))
            drawCircle(color = Color.Black, radius = r, center = powerUp.position + Offset(r * 1.5f, 0f))
            drawCircle(color = Color.Black, radius = r, center = powerUp.position + Offset(0f, -r * 1.5f))
        }
    }
}

fun DrawScope.drawActiveEffectIndicator(
    activeEffect: ActiveEffect,
    leftPaddle: Paddle,
    rightPaddle: Paddle,
    gameTimeMs: Long
) {
    val paddle = if (activeEffect.targetIsLeft) leftPaddle else rightPaddle
    val color = when (activeEffect.type) {
        PowerUpType.WIDER_PADDLE -> PowerUpGreen
        PowerUpType.MULTIBALL -> PowerUpYellow
        PowerUpType.PADDLE_SHRINK -> PowerUpRed
    }

    val pulse = (sin(gameTimeMs * 0.008f) * 0.3f + 0.7f).toFloat()
    val centerX = paddle.position.x + paddle.size.width / 2
    val centerY = paddle.position.y + paddle.size.height / 2

    // Glow behind paddle
    drawOval(
        color = color.copy(alpha = 0.25f * pulse),
        topLeft = Offset(
            centerX - paddle.size.width * 1.5f,
            paddle.position.y - 10f
        ),
        size = androidx.compose.ui.geometry.Size(
            paddle.size.width * 3f,
            paddle.size.height + 20f
        )
    )

    // Border outline
    drawRect(
        color = color.copy(alpha = 0.6f * pulse),
        topLeft = paddle.position,
        size = paddle.size,
        style = Stroke(width = 3f)
    )
}
