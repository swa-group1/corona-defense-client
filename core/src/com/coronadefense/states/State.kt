package com.coronadefense.states

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH

abstract class State(protected var stateManager: StateManager) {
    protected val camera: OrthographicCamera = OrthographicCamera()
    init {
      camera.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT)
    }
    abstract fun update(deltaTime: Float)
    abstract fun render(sprites: SpriteBatch)
    abstract fun dispose()
}