package com.coronadefense.states.menuStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.StateManager
import com.coronadefense.states.InputState
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Textures

/**
 * State to show a tutorial of how to play Corona Defense.
 * Extends InputState to allow clicking the back button.
 * @param stateManager Manager of all game states.
 */
class TutorialState(
  stateManager: StateManager
): InputState(stateManager){
  private val backButton = BackButton("MainMenu", stateManager, stage)

  override fun update(deltaTime: Float) {
    backButton.update()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(Textures.background("tutorial"), 0f, 0f, GAME_WIDTH, GAME_HEIGHT)
    backButton.render(sprites)

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    backButton.dispose()

    println("TutorialState disposed")
  }
}