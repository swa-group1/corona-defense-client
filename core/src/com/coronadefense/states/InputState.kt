package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH

/**
 * Abstract class for states that need user input.
 * Extends the State abstract class for the most basic common state functionality.
 * @param stateManager Manager of all states.
 */
abstract class InputState(
  stateManager: StateManager
) : State(stateManager) {
  // libGDX viewport and stage, making the stage protected for use by subclasses.
  private val viewport: Viewport = StretchViewport(GAME_WIDTH, GAME_HEIGHT, camera)
  protected val stage: Stage = Stage(viewport, Game.sprites)

  // A list of buttons to make disposal of listeners easier.
  // All subclasses that add buttons must add them to this list for listener disposal.
  protected val buttons: MutableList<Image> = mutableListOf()

  init {
    // Adds the stage as an input processor.
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
  }

  fun draw() {
    // Draws all 'actors' (mainly buttons) on the stage.
    stage.draw()
  }

  override fun dispose() {
    // Removes the stage as an input processor.
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }

    stage.clear()
    stage.dispose()

    // Clears listeners from all buttons.
    for (button in buttons) {
      button.clearListeners()
    }

    println("Stage disposed")
  }
}