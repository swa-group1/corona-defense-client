package com.coronadefense.states.lobby

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.Lobby
import com.coronadefense.states.State

class LobbyState(stateManager: GameStateManager, lobby: Lobby): State(stateManager)  {

    init {
        camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
    private val stage: Stage = Stage(viewport, Game.batch)
    private val background: Texture = Texture("background.jpg")

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