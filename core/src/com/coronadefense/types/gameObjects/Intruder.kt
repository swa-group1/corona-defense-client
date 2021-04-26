package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.types.GameStage
import com.coronadefense.utils.Textures

class Intruder(
  private val spriteNumber: Int,
  startPosition: Float,
  private val endPosition: Float,
  time: Float,
  private val gameStage: GameStage
) : MovingGameObject {
  var currentPathPosition = startPosition
  private val speed = (endPosition - startPosition) / time

  override fun draw(sprites: SpriteBatch) {
    if (currentPathPosition <= endPosition) {
      val position = gameStage.getPointAlongPath(currentPathPosition.toDouble())
      sprites.draw(
        Textures.intruder(spriteNumber),
        position.X.toFloat() * gameStage.tileWidth - (gameStage.tileWidth / 2),
        position.Y.toFloat() * gameStage.tileHeight - (gameStage.tileHeight / 2), gameStage.tileWidth, gameStage.tileHeight)
    }
  }

  override fun update(deltaTime: Float) {
    if (currentPathPosition <= endPosition) {
      currentPathPosition += speed * deltaTime
    }
  }

  override fun dispose() { /* Texture disposal handled by Textures */ }
}