package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.types.GameStage
import com.coronadefense.utils.Textures

/**
 * Class for rendering intruders in the wave phase of the game.
 * @param spriteNumber The number corresponding to this intruder's sprite (determined backend).
 * @param startPosition The length along the game map's path to start the rendering.
 * @param endPosition The length along the game map's path to end the rendering.
 * @param time The time between the startPosition and the endPosition.
 * @param gameStage The game map, for use in calculations.
 */
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
      // Uses GameStage's getPointAlongPath utility function for translating path lengths to coordinates.
      val position = gameStage.getPointAlongPath(currentPathPosition.toDouble())

      // Draws the intruder's texture based on its spriteNumber
      // Translates getPointAlongPath's tile position to actual game positions
      sprites.draw(
        Textures.intruder(spriteNumber),
        position.X.toFloat() * gameStage.tileWidth - (gameStage.tileWidth * 0.5f),
        position.Y.toFloat() * gameStage.tileHeight - (gameStage.tileHeight * 0.5f),
        gameStage.tileWidth, gameStage.tileHeight
      )
    }
  }

  override fun update(deltaTime: Float) {
    if (currentPathPosition <= endPosition) {
      currentPathPosition += speed * deltaTime
    }
  }

  override fun dispose() { /* Texture disposal handled by Textures */ }
}