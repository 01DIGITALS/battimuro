package io.github.digitals01.battimuro.game

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

enum class ObstacleMovement {
    NONE,
    VERTICAL,
    HORIZONTAL
}

data class ObstacleDef(
    val centerX: Float,
    val centerY: Float,
    val width: Float,
    val height: Float,
    val movement: ObstacleMovement = ObstacleMovement.NONE,
    val speed: Float = 0f,
    val range: Float = 0f
)

data class Obstacle(
    var position: Offset,
    val size: Size,
    val movement: ObstacleMovement,
    val speed: Float,
    val range: Float,
    val anchorPosition: Offset
)

enum class GameLevel(val displayName: String, val obstacles: List<ObstacleDef>) {
    NONE("NESSUNO", emptyList()),

    LEVEL_1("LIVELLO 1", listOf(
        ObstacleDef(0.5f, 0.5f, 0.04f, 0.15f)
    )),

    LEVEL_2("LIVELLO 2", listOf(
        ObstacleDef(0.35f, 0.3f, 0.035f, 0.12f),
        ObstacleDef(0.65f, 0.7f, 0.035f, 0.12f)
    )),

    LEVEL_3("LIVELLO 3", listOf(
        ObstacleDef(0.5f, 0.5f, 0.04f, 0.14f),
        ObstacleDef(0.3f, 0.35f, 0.03f, 0.10f, ObstacleMovement.VERTICAL, 0.00015f, 0.15f),
        ObstacleDef(0.7f, 0.65f, 0.03f, 0.10f, ObstacleMovement.VERTICAL, 0.00015f, 0.15f)
    )),

    LEVEL_4("LIVELLO 4", listOf(
        ObstacleDef(0.5f, 0.3f, 0.035f, 0.10f, ObstacleMovement.VERTICAL, 0.0002f, 0.18f),
        ObstacleDef(0.5f, 0.7f, 0.035f, 0.10f, ObstacleMovement.VERTICAL, 0.0002f, 0.18f),
        ObstacleDef(0.3f, 0.5f, 0.03f, 0.08f, ObstacleMovement.HORIZONTAL, 0.00012f, 0.08f),
        ObstacleDef(0.7f, 0.5f, 0.03f, 0.08f, ObstacleMovement.HORIZONTAL, 0.00012f, 0.08f)
    )),

    LEVEL_5("LIVELLO 5", listOf(
        ObstacleDef(0.5f, 0.5f, 0.035f, 0.12f, ObstacleMovement.VERTICAL, 0.00025f, 0.20f),
        ObstacleDef(0.35f, 0.25f, 0.03f, 0.08f, ObstacleMovement.VERTICAL, 0.0003f, 0.15f),
        ObstacleDef(0.65f, 0.75f, 0.03f, 0.08f, ObstacleMovement.VERTICAL, 0.0003f, 0.15f),
        ObstacleDef(0.25f, 0.5f, 0.025f, 0.07f, ObstacleMovement.HORIZONTAL, 0.0002f, 0.06f),
        ObstacleDef(0.75f, 0.5f, 0.025f, 0.07f, ObstacleMovement.HORIZONTAL, 0.0002f, 0.06f)
    ));
}
