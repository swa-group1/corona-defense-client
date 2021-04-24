package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.coronadefense.types.GameStage
import com.coronadefense.utils.Textures

class Projectile(
  spriteNumber: Int,
  startTileX: Int,
  startTileY: Int,
  endPosition: Float,
  private val time: Float,
  private val gameStage: GameStage
) : MovingGameObject {
  override val texture = Texture(Textures.projectile(spriteNumber))

  val startX : Float = startTileX.toFloat() + 0.5F
  val startY : Float = startTileY.toFloat() + 0.5F

  private val currentPosition = Vector2(startX, startY)
  private val targetPosition = gameStage.getPointAlongPath(endPosition.toDouble())

  private var currentTime = 0f

  override fun draw(sprites: SpriteBatch) {
    if (currentTime <= time) {
      sprites.draw(
        texture,
        currentPosition.x * gameStage.tileWidth - (gameStage.tileWidth / 2),
        currentPosition.y * gameStage.tileHeight - (gameStage.tileHeight / 2), gameStage.tileWidth, gameStage.tileHeight
      )
    }
  }

  override fun update(deltaTime: Float) {
    if (currentTime <= time) {
      currentTime += deltaTime
      val s = currentTime / time
      currentPosition.x = startX + (targetPosition.X.toFloat() - startX) * s
      currentPosition.y = startY + (targetPosition.Y.toFloat() - startY) * s
    }
  }

  override fun dispose() {
    texture.dispose()
  }
}