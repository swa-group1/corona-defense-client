package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.states.PlayStateWave

class PauseState (stateManager: GameStateManager): State(stateManager){
    init {
        camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
    private val stage: Stage = Stage(viewport, Game.batch)

    override fun handleInput() {
        if (Gdx.input.justTouched()) {
            stateManager.set(PlayStateWave(stateManager))
            dispose()
        }
    }

    override fun update(deltaTime: Float) {
        handleInput()
    }

    override fun render(sprites: SpriteBatch) {
        sprites.projectionMatrix = camera.combined
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
    }

}