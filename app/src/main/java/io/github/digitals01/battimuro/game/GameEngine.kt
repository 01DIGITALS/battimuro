package io.github.digitals01.battimuro.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
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

enum class PowerUpType { WIDER_PADDLE, MULTIBALL, PADDLE_SHRINK }

data class PowerUp(val type: PowerUpType, val position: Offset, val radius: Float = 30f)

enum class LastTouch { LEFT, RIGHT, NONE }

data class ActiveEffect(val type: PowerUpType, val targetIsLeft: Boolean)

class GameEngine(
    private val width: Float,
    private val height: Float,
    val gameMode: GameMode,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val playerIsLeft: Boolean = true,
    val gameLevel: GameLevel = GameLevel.NONE,
    val powerUpsEnabled: Boolean = false
) {
    var playerScore by mutableStateOf(0)
        private set
    var cpuScore by mutableStateOf(0)
        private set
    var status by mutableStateOf(GameStatus.WAITING)
        private set

    var balls by mutableStateOf(listOf(Ball(position = Offset(width / 2, height / 2), velocity = Offset(0f, 0f))))
        private set
    val ball: Ball get() = balls.first()

    var ballTrails by mutableStateOf(listOf(listOf<Offset>()))
        private set
    val ballTrail: List<Offset> get() = ballTrails.first()
    private val maxTrailSize = 12

    var gameTimeMs by mutableStateOf(0L)
        private set

    var obstacles by mutableStateOf(createObstacles())
        private set

    // Power-up state
    var powerUp by mutableStateOf<PowerUp?>(null)
        private set
    var activeEffect by mutableStateOf<ActiveEffect?>(null)
        private set
    var lastTouch by mutableStateOf(LastTouch.NONE)
        private set
    private var powerUpCooldownMs = 0L
    private val powerUpSpawnDelay = 5000L
    private val powerUpRespawnDelay = 3000L

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
        if (powerUpsEnabled) {
            powerUpCooldownMs = powerUpSpawnDelay
        }
    }

    fun pause() {
        if (status == GameStatus.PLAYING) status = GameStatus.PAUSED
    }

    fun resume() {
        if (status == GameStatus.PAUSED) status = GameStatus.PLAYING
    }

    private fun resetBall() {
        ballTrails = listOf(emptyList())
        lastTouch = LastTouch.NONE
        deactivateEffect()

        val speed = 15f
        val vx = if (Random.nextBoolean()) speed else -speed
        val vy = (Random.nextFloat() - 0.5f) * speed

        balls = listOf(
            Ball(
                position = Offset(width / 2, height / 2),
                velocity = Offset(vx, vy)
            )
        )

        if (powerUpsEnabled) {
            powerUp = null
            powerUpCooldownMs = powerUpRespawnDelay
        }
    }

    private fun spawnPowerUp() {
        val type = PowerUpType.entries.random()
        val x = width * (0.2f + Random.nextFloat() * 0.6f)
        val y = height * (0.15f + Random.nextFloat() * 0.7f)
        powerUp = PowerUp(type = type, position = Offset(x, y))
    }

    private fun checkPowerUpCollision() {
        val pu = powerUp ?: return
        for (b in balls) {
            val dx = b.position.x - pu.position.x
            val dy = b.position.y - pu.position.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist < b.radius + pu.radius) {
                activatePowerUp(pu.type)
                powerUp = null
                powerUpCooldownMs = powerUpRespawnDelay
                return
            }
        }
    }

    private fun activatePowerUp(type: PowerUpType) {
        val targetIsLeft = when (lastTouch) {
            LastTouch.LEFT -> true
            LastTouch.RIGHT -> false
            LastTouch.NONE -> Random.nextBoolean()
        }

        deactivateEffect()

        when (type) {
            PowerUpType.WIDER_PADDLE -> {
                activeEffect = ActiveEffect(PowerUpType.WIDER_PADDLE, targetIsLeft)
                resizePaddle(targetIsLeft, paddleHeight * 1.5f)
            }
            PowerUpType.PADDLE_SHRINK -> {
                activeEffect = ActiveEffect(PowerUpType.PADDLE_SHRINK, targetIsLeft)
                resizePaddle(targetIsLeft, paddleHeight * 0.5f)
            }
            PowerUpType.MULTIBALL -> {
                activeEffect = ActiveEffect(PowerUpType.MULTIBALL, targetIsLeft)
                spawnExtraBalls(2)
            }
        }
    }

    private fun resizePaddle(isLeft: Boolean, newHeight: Float) {
        if (isLeft) {
            val centerY = leftPaddle.position.y + leftPaddle.size.height / 2
            val newY = (centerY - newHeight / 2).coerceIn(0f, height - newHeight)
            leftPaddle = leftPaddle.copy(
                position = leftPaddle.position.copy(y = newY),
                size = Size(paddleWidth, newHeight)
            )
        } else {
            val centerY = rightPaddle.position.y + rightPaddle.size.height / 2
            val newY = (centerY - newHeight / 2).coerceIn(0f, height - newHeight)
            rightPaddle = rightPaddle.copy(
                position = rightPaddle.position.copy(y = newY),
                size = Size(paddleWidth, newHeight)
            )
        }
    }

    private fun deactivateEffect() {
        val effect = activeEffect ?: return
        when (effect.type) {
            PowerUpType.WIDER_PADDLE, PowerUpType.PADDLE_SHRINK -> {
                resizePaddle(effect.targetIsLeft, paddleHeight)
            }
            PowerUpType.MULTIBALL -> { /* balls reset by resetBall() */ }
        }
        activeEffect = null
    }

    private fun spawnExtraBalls(count: Int) {
        val primary = balls.first()
        val newBalls = (1..count).map { i ->
            val angleOffset = if (i == 1) 0.5f else -0.5f
            Ball(
                position = primary.position,
                velocity = Offset(
                    primary.velocity.x,
                    primary.velocity.y + primary.velocity.x * angleOffset
                ),
                radius = primary.radius
            )
        }
        balls = balls + newBalls
        ballTrails = ballTrails + newBalls.map { emptyList<Offset>() }
    }

    fun update(deltaTime: Long) {
        if (status != GameStatus.PLAYING) return

        gameTimeMs += deltaTime

        // Power-up cooldown/spawn
        if (powerUpsEnabled && powerUp == null) {
            powerUpCooldownMs -= deltaTime
            if (powerUpCooldownMs <= 0) {
                spawnPowerUp()
            }
        }

        // Update trails
        ballTrails = balls.mapIndexed { index, b ->
            val trail = ballTrails.getOrElse(index) { emptyList() }
            (trail + b.position).takeLast(maxTrailSize)
        }

        // Move all balls
        balls = balls.map { b ->
            b.copy(position = b.position + b.velocity)
        }

        // Wall bounces
        balls = balls.map { b ->
            var updated = b
            if (updated.position.y - updated.radius < 0) {
                updated = updated.copy(
                    position = updated.position.copy(y = updated.radius),
                    velocity = updated.velocity.copy(y = -updated.velocity.y)
                )
            }
            if (updated.position.y + updated.radius > height) {
                updated = updated.copy(
                    position = updated.position.copy(y = height - updated.radius),
                    velocity = updated.velocity.copy(y = -updated.velocity.y)
                )
            }
            updated
        }

        updateObstacles()

        // Obstacle collisions
        balls = balls.map { b -> checkObstacleCollisionForBall(b) }

        // Paddle collisions
        balls = balls.map { b ->
            var updated = checkPaddleCollisionForBall(b, leftPaddle, isLeft = true)
            updated = checkPaddleCollisionForBall(updated, rightPaddle, isLeft = false)
            updated
        }

        // Power-up collision
        if (powerUpsEnabled) {
            checkPowerUpCollision()
        }

        // Scoring: any ball exit -> score + full reset
        val anyExitLeft = balls.any { it.position.x < 0 }
        val anyExitRight = balls.any { it.position.x > width }

        if (anyExitLeft) {
            cpuScore++
            resetBall()
        } else if (anyExitRight) {
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

    private fun checkPaddleCollisionForBall(b: Ball, paddle: Paddle, isLeft: Boolean): Ball {
        val ballRect = Rect(
            b.position.x - b.radius,
            b.position.y - b.radius,
            b.position.x + b.radius,
            b.position.y + b.radius
        )
        val paddleRect = Rect(paddle.position, paddle.size)

        if (ballRect.overlaps(paddleRect)) {
            lastTouch = if (isLeft) LastTouch.LEFT else LastTouch.RIGHT

            var updated = b.copy(velocity = b.velocity.copy(x = -b.velocity.x))
            val hitPoint = b.position.y - (paddle.position.y + paddle.size.height / 2)
            updated = updated.copy(velocity = updated.velocity.copy(y = updated.velocity.y + hitPoint * 0.1f))
            updated = updated.copy(velocity = updated.velocity * 1.05f)
            return updated
        }
        return b
    }

    private fun checkObstacleCollisionForBall(b: Ball): Ball {
        val ballRect = Rect(
            b.position.x - b.radius,
            b.position.y - b.radius,
            b.position.x + b.radius,
            b.position.y + b.radius
        )

        for (obstacle in obstacles) {
            val obstacleRect = Rect(obstacle.position, obstacle.size)

            if (ballRect.overlaps(obstacleRect)) {
                val overlapLeft = (b.position.x + b.radius) - obstacle.position.x
                val overlapRight = (obstacle.position.x + obstacle.size.width) - (b.position.x - b.radius)
                val overlapTop = (b.position.y + b.radius) - obstacle.position.y
                val overlapBottom = (obstacle.position.y + obstacle.size.height) - (b.position.y - b.radius)

                val minOverlapX = min(overlapLeft, overlapRight)
                val minOverlapY = min(overlapTop, overlapBottom)

                var updated = b
                if (minOverlapX < minOverlapY) {
                    updated = updated.copy(velocity = updated.velocity.copy(x = -updated.velocity.x))
                    updated = if (overlapLeft < overlapRight) {
                        updated.copy(position = updated.position.copy(x = obstacle.position.x - updated.radius))
                    } else {
                        updated.copy(position = updated.position.copy(x = obstacle.position.x + obstacle.size.width + updated.radius))
                    }
                } else {
                    updated = updated.copy(velocity = updated.velocity.copy(y = -updated.velocity.y))
                    updated = if (overlapTop < overlapBottom) {
                        updated.copy(position = updated.position.copy(y = obstacle.position.y - updated.radius))
                    } else {
                        updated.copy(position = updated.position.copy(y = obstacle.position.y + obstacle.size.height + updated.radius))
                    }
                }
                return updated
            }
        }
        return b
    }

    private fun updateCpuPaddle(paddle: Paddle, isRight: Boolean) {
        // Track the closest incoming ball
        val incomingBalls = balls.filter { b ->
            if (isRight) b.velocity.x > 0 else b.velocity.x < 0
        }

        val targetBall = if (incomingBalls.isNotEmpty()) {
            if (isRight) {
                incomingBalls.maxByOrNull { it.position.x }
            } else {
                incomingBalls.minByOrNull { it.position.x }
            }
        } else null

        val targetY = if (targetBall != null) {
            targetBall.position.y - paddle.size.height / 2
        } else {
            height / 2
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

    private fun createObstacles(): List<Obstacle> {
        return gameLevel.obstacles.map { def ->
            val pixelWidth = def.width * width
            val pixelHeight = def.height * height
            val pixelX = def.centerX * width - pixelWidth / 2
            val pixelY = def.centerY * height - pixelHeight / 2
            Obstacle(
                position = Offset(pixelX, pixelY),
                size = Size(pixelWidth, pixelHeight),
                movement = def.movement,
                speed = def.speed,
                range = when (def.movement) {
                    ObstacleMovement.VERTICAL -> def.range * height
                    ObstacleMovement.HORIZONTAL -> def.range * width
                    ObstacleMovement.NONE -> 0f
                },
                anchorPosition = Offset(pixelX + pixelWidth / 2, pixelY + pixelHeight / 2)
            )
        }
    }

    private fun updateObstacles() {
        obstacles = obstacles.map { obstacle ->
            when (obstacle.movement) {
                ObstacleMovement.NONE -> obstacle
                ObstacleMovement.VERTICAL -> {
                    val offsetY = sin(gameTimeMs.toDouble() * obstacle.speed) * obstacle.range
                    val newY = (obstacle.anchorPosition.y + offsetY.toFloat() - obstacle.size.height / 2)
                        .coerceIn(0f, height - obstacle.size.height)
                    obstacle.copy(position = Offset(obstacle.position.x, newY))
                }
                ObstacleMovement.HORIZONTAL -> {
                    val offsetX = sin(gameTimeMs.toDouble() * obstacle.speed) * obstacle.range
                    val newX = (obstacle.anchorPosition.x + offsetX.toFloat() - obstacle.size.width / 2)
                        .coerceIn(0f, width - obstacle.size.width)
                    obstacle.copy(position = Offset(newX, obstacle.position.y))
                }
            }
        }
    }

    fun updatePaddle(y: Float, isLeftPaddle: Boolean) {
        val paddle = if (isLeftPaddle) leftPaddle else rightPaddle
        val clampY = (y - paddle.size.height / 2).coerceIn(0f, height - paddle.size.height)

        if (isLeftPaddle) {
            leftPaddle = leftPaddle.copy(position = leftPaddle.position.copy(y = clampY))
        } else {
            rightPaddle = rightPaddle.copy(position = rightPaddle.position.copy(y = clampY))
        }
    }
}
