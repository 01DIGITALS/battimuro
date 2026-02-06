package io.github.digitals01.battimuro.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.digitals01.battimuro.game.Paddle
import io.github.digitals01.battimuro.game.PaddleStyle
import io.github.digitals01.battimuro.ui.theme.*
import kotlin.math.sin

fun DrawScope.drawPaddle(
    leftPaddle: Paddle,
    rightPaddle: Paddle,
    style: PaddleStyle,
    gameTimeMs: Long
) {
    when (style) {
        PaddleStyle.NEON -> drawPaddleNeon(leftPaddle, rightPaddle)
        PaddleStyle.MINIMAL -> drawPaddleMinimal(leftPaddle, rightPaddle)
        PaddleStyle.SOLID -> drawPaddleSolid(leftPaddle, rightPaddle)
        PaddleStyle.PULSE -> drawPaddlePulse(leftPaddle, rightPaddle, gameTimeMs)
        PaddleStyle.PRISMA -> drawPaddlePrisma(leftPaddle, rightPaddle, gameTimeMs)
        PaddleStyle.GHOST -> drawPaddleGhost(leftPaddle, rightPaddle)
        PaddleStyle.PIXEL -> drawPaddlePixel(leftPaddle, rightPaddle)
        PaddleStyle.PLASMA -> drawPaddlePlasma(leftPaddle, rightPaddle, gameTimeMs)
        PaddleStyle.ICE -> drawPaddleIce(leftPaddle, rightPaddle)
        PaddleStyle.GOLD -> drawPaddleGold(leftPaddle, rightPaddle, gameTimeMs)
    }
}

private fun DrawScope.drawPaddleNeon(left: Paddle, right: Paddle) {
    drawRect(
        brush = Brush.verticalGradient(listOf(NeonCyan, NeonMagenta)),
        topLeft = left.position,
        size = left.size
    )
    drawRect(
        brush = Brush.verticalGradient(listOf(NeonMagenta, NeonCyan)),
        topLeft = right.position,
        size = right.size
    )
}

private fun DrawScope.drawPaddleMinimal(left: Paddle, right: Paddle) {
    drawRect(
        color = Color.White,
        topLeft = left.position,
        size = left.size,
        style = Stroke(width = 2f)
    )
    drawRect(
        color = Color.White,
        topLeft = right.position,
        size = right.size,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawPaddleSolid(left: Paddle, right: Paddle) {
    drawRect(color = NeonCyan, topLeft = left.position, size = left.size)
    drawRect(color = NeonMagenta, topLeft = right.position, size = right.size)
}

private fun DrawScope.drawPaddlePulse(left: Paddle, right: Paddle, gameTimeMs: Long) {
    val brightness = (sin(gameTimeMs * 0.004f).toFloat() * 0.4f + 0.6f)
    drawRect(color = NeonCyan.copy(alpha = brightness), topLeft = left.position, size = left.size)
    drawRect(color = NeonMagenta.copy(alpha = brightness), topLeft = right.position, size = right.size)
}

private fun DrawScope.drawPaddlePrisma(left: Paddle, right: Paddle, gameTimeMs: Long) {
    val hue1 = (gameTimeMs * 0.08f) % 360f
    val hue2 = (hue1 + 180f) % 360f
    val brush = Brush.verticalGradient(listOf(Color.hsl(hue1, 1f, 0.5f), Color.hsl(hue2, 1f, 0.5f)))
    drawRect(brush = brush, topLeft = left.position, size = left.size)
    drawRect(brush = brush, topLeft = right.position, size = right.size)
}

private fun DrawScope.drawPaddleGhost(left: Paddle, right: Paddle) {
    for (paddle in listOf(left, right)) {
        drawRect(
            color = Color.White.copy(alpha = 0.15f),
            topLeft = paddle.position,
            size = paddle.size
        )
        drawRect(
            color = Color.White.copy(alpha = 0.8f),
            topLeft = paddle.position,
            size = paddle.size,
            style = Stroke(2f)
        )
    }
}

private fun DrawScope.drawPaddlePixel(left: Paddle, right: Paddle) {
    for (paddle in listOf(left, right)) {
        drawRect(color = Color.White, topLeft = paddle.position, size = paddle.size)
        val steps = (paddle.size.height / 10f).toInt()
        for (i in 1 until steps) {
            val y = paddle.position.y + i * 10f
            drawLine(
                Color.Black,
                Offset(paddle.position.x, y),
                Offset(paddle.position.x + paddle.size.width, y),
                1f
            )
        }
    }
}

private fun DrawScope.drawPaddlePlasma(left: Paddle, right: Paddle, gameTimeMs: Long) {
    val borderAlpha = (sin(gameTimeMs * 0.01f).toFloat() * 0.3f + 0.7f)
    for (paddle in listOf(left, right)) {
        drawRect(
            color = NeonMagenta.copy(alpha = 0.6f),
            topLeft = paddle.position,
            size = paddle.size
        )
        drawRect(
            color = NeonCyan.copy(alpha = borderAlpha),
            topLeft = paddle.position,
            size = paddle.size,
            style = Stroke(3f)
        )
    }
}

private fun DrawScope.drawPaddleIce(left: Paddle, right: Paddle) {
    for (paddle in listOf(left, right)) {
        val expanded = Size(paddle.size.width + 6f, paddle.size.height + 6f)
        drawRect(
            color = IceCyan.copy(alpha = 0.15f),
            topLeft = Offset(paddle.position.x - 3f, paddle.position.y - 3f),
            size = expanded
        )
        drawRect(
            brush = Brush.verticalGradient(listOf(IceCyan, IceBlue)),
            topLeft = paddle.position,
            size = paddle.size
        )
    }
}

private fun DrawScope.drawPaddleGold(left: Paddle, right: Paddle, gameTimeMs: Long) {
    val shimmer = (sin(gameTimeMs * 0.005f).toFloat() * 0.5f + 0.5f)
    val brush = Brush.verticalGradient(
        listOf(
            lerp(GoldBase, GoldLight, shimmer),
            GoldBase,
            lerp(GoldBase, GoldLight, 1f - shimmer)
        )
    )
    drawRect(brush = brush, topLeft = left.position, size = left.size)
    drawRect(brush = brush, topLeft = right.position, size = right.size)
}
