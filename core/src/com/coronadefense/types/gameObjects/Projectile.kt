package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Affine2
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.coronadefense.types.GameStage
import com.coronadefense.utils.Textures
import ktx.math.minus
import ktx.math.plus
import ktx.math.times

class Projectile(
  spriteNumber: Int,
  startTileX: Int,
  startTileY: Int,
  endPosition: Float,
  private val time: Float,
  private val gameStage: GameStage
) : MovingGameObject {
  override val texture = Texture(Textures.projectile(spriteNumber))
  private val textureRegion: TextureRegion = TextureRegion(texture)

  /** Tile size in pixels. */
  private val tileSize: Vector2 = Vector2(gameStage.tileWidth, gameStage.tileHeight)

  /** Source position in pixel space. */
  private val sourcePosition: Vector2 =
    Vector2(startTileX.toFloat() + 0.5f, startTileY.toFloat() + 0.5f) * tileSize
  /** Target position in pixel space. */
  private val targetPosition: Vector2 =
    gameStage.getPointAlongPath(endPosition.toDouble()).toVector2() * tileSize

  /**
   * Angle in radians this projectile should be turned.
   */
  private val rotationMatrix: Affine2
  init {
    val direction = targetPosition - sourcePosition
    val angle = MathUtils.atan2(direction.y, direction.x) + MathUtils.HALF_PI
    val offsetMatrix = Affine2().translate(tileSize * -0.5f)
    rotationMatrix = offsetMatrix.preRotateRad(angle)
  }

  /** Current position in pixel space. */
  private var currentPosition: Vector2 = sourcePosition
  private var currentTime = 0f

  override fun draw(sprites: SpriteBatch) {
    if (currentTime <= time) {
      sprites.draw(
        textureRegion,
        gameStage.tileWidth,
        gameStage.tileHeight,
        Affine2(rotationMatrix).preTranslate(currentPosition)
      )
    }
  }

  override fun update(deltaTime: Float) {
    if (currentTime <= time) {
      currentTime += deltaTime
      val lerpFactor = currentTime / time
      currentPosition = sourcePosition + (targetPosition - sourcePosition) * lerpFactor
    }
  }

  override fun dispose() {
    texture.dispose()
  }
}