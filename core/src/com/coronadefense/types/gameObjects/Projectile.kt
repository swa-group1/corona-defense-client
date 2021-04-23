package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.coronadefense.types.GameStage

class Projectile(
  texturePath: String,
  private val startX: Int,
  private val startY: Int,
  endPosition: Float,
  private val time: Float,
  private val gameStage: GameStage
) : MovingGameObject {
  private val currentPosition = Vector2(startX.toFloat(), startY.toFloat())
  private val targetPosition = gameStage.getPointAlongPath(endPosition.toDouble())

  private var currentTime = 0f

  override val texture = Texture(texturePath)


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
}