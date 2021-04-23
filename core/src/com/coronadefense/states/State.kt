package com.coronadefense.states

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.utils.Constants

abstract class State(protected var stateManager: GameStateManager) {
    protected val camera: OrthographicCamera = OrthographicCamera()
    init {
      camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    }
    abstract fun update(deltaTime: Float)
    abstract fun render(sprites: SpriteBatch)
    abstract fun dispose()
}