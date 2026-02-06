package io.github.digitals01.battimuro.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.abs
import kotlin.random.Random

data class Ball(
    var position: Offset,
    var velocity: Offset,
    val radius: Float = 20f
)

data class Paddle(
    var position: Offset,
    val size: Size,
    val isLeft: Boolean
)

enum class GameStatus {
    WAITING,
    PLAYING,
    PAUSED,
    GAME_OVER
}

enum class GameMode {
    ONE_VS_CPU,
    ONE_VS_ONE
}

enum class Difficulty(val speedFactor: Float, val reactionError: Float) {
    EASY(0.5f, 0.3f),
    MEDIUM(0.8f, 0.1f),
    HARD(1.2f, 0.0f)
}

enum class BallStyle(val isPremium: Boolean = false) {
    NEON,
    MINIMAL,
    FLAME,
    PULSE(isPremium = true),
    PRISMA(isPremium = true),
    GHOST(isPremium = true),
    PIXEL(isPremium = true),
    PLASMA(isPremium = true),
    ICE(isPremium = true),
    GOLD(isPremium = true)
}

enum class PaddleStyle(val isPremium: Boolean = false) {
    NEON,
    MINIMAL,
    SOLID,
    PULSE(isPremium = true),
    PRISMA(isPremium = true),
    GHOST(isPremium = true),
    PIXEL(isPremium = true),
    PLASMA(isPremium = true),
    ICE(isPremium = true),
    GOLD(isPremium = true)
}

class GameEngine(
    private val width: Float,
    private val height: Float,
    val gameMode: GameMode,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val playerIsLeft: Boolean = true
) {
    var playerScore by mutableStateOf(0)
        private set
    var cpuScore by mutableStateOf(0)
        private set
    var status by mutableStateOf(GameStatus.WAITING)
        private set

    var ball by mutableStateOf(Ball(position = Offset(width / 2, height / 2), velocity = Offset(0f, 0f)))
        private set

    var ballTrail by mutableStateOf(listOf<Offset>())
        private set
    private val maxTrailSize = 12

    var gameTimeMs by mutableStateOf(0L)
        private set

    val paddleWidth = 40f
    val paddleHeight = 200f
    val paddleMargin = 50f

    var leftPaddle by mutableStateOf(Paddle(
        position = Offset(paddleMargin, (height - paddleHeight) / 2),
        size = Size(paddleWidth, paddleHeight),
        isLeft = true
    ))
        private set

    var rightPaddle by mutableStateOf(Paddle(
        position = Offset(width - paddleMargin - paddleWidth, (height - paddleHeight) / 2),
        size = Size(paddleWidth, paddleHeight),
        isLeft = false
    ))
        private set

    fun start() {
        resetBall()
        status = GameStatus.PLAYING
    }

    fun pause() {
        if (status == GameStatus.PLAYING) status = GameStatus.PAUSED
    }

    fun resume() {
        if (status == GameStatus.PAUSED) status = GameStatus.PLAYING
    }

    private fun resetBall() {
        ballTrail = emptyList()
        var newBall = ball.copy(position = Offset(width / 2, height / 2))

        val speed = 15f
        val angle = Random.nextFloat() * 3.14f
        val vx = if (Random.nextBoolean()) speed else -speed
        val vy = (Random.nextFloat() - 0.5f) * speed

        newBall.velocity = Offset(vx, vy)
        ball = newBall
    }

    fun update(deltaTime: Long) {
        if (status != GameStatus.PLAYING) return

        gameTimeMs += deltaTime
        ballTrail = (ballTrail + ball.position).takeLast(maxTrailSize)

        val nextPos = ball.position + ball.velocity
        ball = ball.copy(position = nextPos)

        if (ball.position.y - ball.radius < 0) {
            ball = ball.copy(
                position = ball.position.copy(y = ball.radius),
                velocity = ball.velocity.copy(y = -ball.velocity.y)
            )
        }
        if (ball.position.y + ball.radius > height) {
            ball = ball.copy(
                position = ball.position.copy(y = height - ball.radius),
                velocity = ball.velocity.copy(y = -ball.velocity.y)
            )
        }

        checkPaddleCollision(leftPaddle)
        checkPaddleCollision(rightPaddle)

        if (ball.position.x < 0) {
            cpuScore++
            resetBall()
        }
        if (ball.position.x > width) {
            playerScore++
            resetBall()
        }

        if (gameMode == GameMode.ONE_VS_CPU) {
            if (playerIsLeft) {
                updateCpuPaddle(rightPaddle, isRight = true)
            } else {
                updateCpuPaddle(leftPaddle, isRight = false)
            }
        }
    }

    private fun checkPaddleCollision(paddle: Paddle) {
        val ballRect = androidx.compose.ui.geometry.Rect(
            ball.position.x - ball.radius,
            ball.position.y - ball.radius,
            ball.position.x + ball.radius,
            ball.position.y + ball.radius
        )
        val paddleRect = androidx.compose.ui.geometry.Rect(
            paddle.position,
            paddle.size
        )

        if (ballRect.overlaps(paddleRect)) {
            ball = ball.copy(velocity = ball.velocity.copy(x = -ball.velocity.x))

            val hitPoint = ball.position.y - (paddle.position.y + paddle.size.height / 2)
            ball = ball.copy(velocity = ball.velocity.copy(y = ball.velocity.y + hitPoint * 0.1f))

            ball = ball.copy(velocity = ball.velocity * 1.05f)
        }
    }

    private fun updateCpuPaddle(paddle: Paddle, isRight: Boolean) {
        val isIncoming = if (isRight) ball.velocity.x > 0 else ball.velocity.x < 0

        var targetY = height / 2
        if (isIncoming) {
             targetY = ball.position.y - paddle.size.height / 2
        }

        val currentY = paddle.position.y
        val lerpFactor = 0.1f * difficulty.speedFactor

        val newY = currentY + (targetY - currentY) * lerpFactor

        val newPos = paddle.position.copy(
            y = newY.coerceIn(0f, height - paddle.size.height)
        )

        if (isRight) {
            rightPaddle = rightPaddle.copy(position = newPos)
        } else {
            leftPaddle = leftPaddle.copy(position = newPos)
        }
    }

    fun updatePaddle(y: Float, isLeftPaddle: Boolean) {
        val clampY = (y - paddleHeight / 2).coerceIn(0f, height - paddleHeight)

        if (isLeftPaddle) {
            leftPaddle = leftPaddle.copy(position = leftPaddle.position.copy(y = clampY))
        } else {
            rightPaddle = rightPaddle.copy(position = rightPaddle.position.copy(y = clampY))
        }
    }
}
