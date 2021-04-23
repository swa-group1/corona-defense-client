package com.coronadefense.states.gameEndStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.GameStateManager
import com.coronadefense.states.State
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Textures

/**
 * State to show the defeat screen once the Coronavirus prevails.
 * @param stateManager Manager of all game states.
 */
class LoserState(
  stateManager: GameStateManager
): State(stateManager) {
  private val background: Texture = Texture(Textures.background("loss"))

  override fun update(deltaTime: Float) { /* Nothing to update */ }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0f, 0f, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    sprites.end()
  }

  override fun dispose(){
    background.dispose()
  }
}