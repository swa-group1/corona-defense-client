package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.states.PlayStateWave
import com.coronadefense.utils.Constants

class PauseState (stateManager: GameStateManager): State(stateManager){
  init {
    camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.sprites)

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    stage.draw()
  }

  override fun dispose() {
    stage.dispose()
  }

  override fun update(deltaTime: Float) {}
}