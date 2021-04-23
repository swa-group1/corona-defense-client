package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.utils.Constants

abstract class StageState(
  stateManager: GameStateManager
) : State(stateManager) {
  private val viewport: Viewport = StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera)
  protected val stage: Stage = Stage(viewport, Game.sprites)

  // adds the stage as an input processor
  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
  }

  override fun render(sprites: SpriteBatch) {
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
    println("Stage disposed")
  }
}