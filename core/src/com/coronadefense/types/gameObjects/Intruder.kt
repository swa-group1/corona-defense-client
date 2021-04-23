package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.types.GameStage

class Intruder(
  texturePath: String,
  startPosition: Float,
  private val endPosition: Float,
  time: Float,
  private val gameStage: GameStage
) : MovingGameObject {
  override val texture = Texture(texturePath)
  var currentPathPosition = startPosition
  private val speed = (endPosition - startPosition) / time

  override fun draw(sprites: SpriteBatch) {
    if (currentPathPosition <= endPosition) {
      val position = gameStage.getPointAlongPath(currentPathPosition.toDouble())
      sprites.draw(
        texture,
        position.X.toFloat() * gameStage.tileWidth - (gameStage.tileWidth / 2),
        position.Y.toFloat() * gameStage.tileHeight - (gameStage.tileHeight / 2), gameStage.tileWidth, gameStage.tileHeight)
    }
  }

  override fun update(deltaTime: Float) {
    if (currentPathPosition <= endPosition) {
      currentPathPosition += speed * deltaTime
    }
  }
}