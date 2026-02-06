package io.github.digitals01.battimuro.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import io.github.digitals01.battimuro.game.Ball
import io.github.digitals01.battimuro.game.BallStyle
import io.github.digitals01.battimuro.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawBall(
    ball: Ball,
    style: BallStyle,
    trail: List<Offset>,
    gameTimeMs: Long
) {
    when (style) {
        BallStyle.NEON -> drawBallNeon(ball)
        BallStyle.MINIMAL -> drawBallMinimal(ball)
        BallStyle.FLAME -> drawBallFlame(ball, trail)
        BallStyle.PULSE -> drawBallPulse(ball, gameTimeMs)
        BallStyle.PRISMA -> drawBallPrisma(ball, gameTimeMs)
        BallStyle.GHOST -> drawBallGhost(ball, trail)
        BallStyle.PIXEL -> drawBallPixel(ball)
        BallStyle.PLASMA -> drawBallPlasma(ball, gameTimeMs)
        BallStyle.ICE -> drawBallIce(ball)
        BallStyle.GOLD -> drawBallGold(ball, gameTimeMs)
    }
}

private fun DrawScope.drawBallNeon(ball: Ball) {
    drawCircle(
        color = NeonGreen.copy(alpha = 0.3f),
        radius = ball.radius * 2,
        center = ball.position
    )
    drawCircle(
        color = NeonGreen,
        radius = ball.radius,
        center = ball.position
    )
}

private fun DrawScope.drawBallMinimal(ball: Ball) {
    drawCircle(
        color = Color.White,
        radius = ball.radius,
        center = ball.position,
        style = Stroke(width = 2f)
    )
}

private fun DrawScope.drawBallFlame(ball: Ball, trail: List<Offset>) {
    trail.forEachIndexed { index, pos ->
        val progress = (index + 1).toFloat() / (trail.size + 1)
        val trailRadius = ball.radius * progress * 0.8f
        val trailColor = lerp(NeonRed, NeonOrange, progress)
        drawCircle(
            color = trailColor.copy(alpha = progress * 0.5f),
            radius = trailRadius,
            center = pos
        )
    }
    drawCircle(
        color = NeonOrange.copy(alpha = 0.4f),
        radius = ball.radius * 1.8f,
        center = ball.position
    )
    drawCircle(
        color = NeonGreen,
        radius = ball.radius,
        center = ball.position
    )
}

private fun DrawScope.drawBallPulse(ball: Ball, gameTimeMs: Long) {
    val phase = sin(gameTimeMs * 0.005f).toFloat()
    val radius = ball.radius * (1f + 0.3f * phase)
    drawCircle(
        color = NeonMagenta.copy(alpha = 0.2f),
        radius = radius * 2f,
        center = ball.position
    )
    drawCircle(
        color = NeonMagenta,
        radius = radius,
        center = ball.position
    )
}

private fun DrawScope.drawBallPrisma(ball: Ball, gameTimeMs: Long) {
    val hue = (gameTimeMs * 0.1f) % 360f
    val color = Color.hsl(hue, 1f, 0.5f)
    drawCircle(
        color = color.copy(alpha = 0.3f),
        radius = ball.radius * 2f,
        center = ball.position
    )
    drawCircle(
        color = color,
        radius = ball.radius,
        center = ball.position
    )
}

private fun DrawScope.drawBallGhost(ball: Ball, trail: List<Offset>) {
    trail.forEachIndexed { index, pos ->
        val alpha = (index + 1).toFloat() / (trail.size + 1) * 0.15f
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = ball.radius,
            center = pos
        )
    }
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = ball.radius,
        center = ball.position
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.8f),
        radius = ball.radius,
        center = ball.position,
        style = Stroke(2f)
    )
}

private fun DrawScope.drawBallPixel(ball: Ball) {
    val side = ball.radius * 2f
    val topLeft = Offset(ball.position.x - ball.radius, ball.position.y - ball.radius)
    drawRect(
        color = Color.White,
        topLeft = topLeft,
        size = Size(side, side)
    )
}

private fun DrawScope.drawBallPlasma(ball: Ball, gameTimeMs: Long) {
    drawCircle(
        color = NeonMagenta,
        radius = ball.radius,
        center = ball.position
    )
    for (i in 0 until 4) {
        val angle = (gameTimeMs * 0.008f) + (i * PI.toFloat() / 2f)
        val orbitRadius = ball.radius * 2.5f
        val px = ball.position.x + cos(angle) * orbitRadius
        val py = ball.position.y + sin(angle) * orbitRadius
        drawCircle(
            color = NeonCyan.copy(alpha = 0.7f),
            radius = 4f,
            center = Offset(px, py)
        )
    }
}

private fun DrawScope.drawBallIce(ball: Ball) {
    drawCircle(
        color = IceCyan.copy(alpha = 0.15f),
        radius = ball.radius * 2.5f,
        center = ball.position
    )
    drawCircle(
        color = IceBlue.copy(alpha = 0.4f),
        radius = ball.radius * 1.4f,
        center = ball.position
    )
    drawCircle(
        color = IceCyan,
        radius = ball.radius,
        center = ball.position
    )
}

private fun DrawScope.drawBallGold(ball: Ball, gameTimeMs: Long) {
    val shimmer = (sin(gameTimeMs * 0.006f).toFloat() * 0.5f + 0.5f)
    val color = lerp(GoldBase, GoldLight, shimmer)
    drawCircle(
        color = GoldBase.copy(alpha = 0.2f),
        radius = ball.radius * 2f,
        center = ball.position
    )
    drawCircle(
        color = color,
        radius = ball.radius,
        center = ball.position
    )
}
