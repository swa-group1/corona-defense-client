package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.g2d.SpriteBatch

/**
 * Common interface for all moving game objects rendered in PlayStateWave.
 */
interface MovingGameObject {
  fun draw(sprites: SpriteBatch)
  fun update(deltaTime: Float)
  fun dispose()
}