package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH

abstract class InputState(
  stateManager: StateManager
) : State(stateManager) {
  private val viewport: Viewport = StretchViewport(GAME_WIDTH, GAME_HEIGHT, camera)
  protected val stage: Stage = Stage(viewport, Game.sprites)
  protected val buttons: MutableList<Image> = mutableListOf()

  // adds the stage as an input processor
  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
  }

  fun draw() {
    stage.draw()
  }

  // removes the stage as an input processor, clears the stage and disposes it
  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }

    stage.clear()
    stage.dispose()

    for (button in buttons) {
      button.clearListeners()
    }

    println("Stage disposed")
  }
}