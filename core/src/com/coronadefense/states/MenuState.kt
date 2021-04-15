package com.coronadefense.states

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager

class MenuState(stateManager: GameStateManager): State(stateManager) {
    init {
        camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
    private val stage: Stage = Stage(viewport, Game.batch)

    private val background = Texture("initiate_game_state.jpg")

    override fun handleInput() {
        TODO("Not yet implemented")
    }

    override fun update(deltaTime: Float) {
        TODO("Not yet implemented")
    }

    override fun render(sprites: SpriteBatch) {
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }


}