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

    }

    override fun update(deltaTime: Float) {
        handleInput()
    }

    override fun render(sprites: SpriteBatch) {
        sprites.projectionMatrix = camera.combined
        sprites.begin()
        sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
        sprites.end()
        stage.draw()
    }

    override fun dispose() {
        background.dispose()
        println("Menu State Disposed")
    }


}