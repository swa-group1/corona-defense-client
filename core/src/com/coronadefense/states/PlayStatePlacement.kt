package com.coronadefense.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.GameStateManager


abstract class PlayStatePlacement(stateManager: GameStateManager) : State(stateManager) {
    protected val backButton: BackButton = BackButton(stateManager)
    override fun render(sprites: SpriteBatch) {
        backButton.draw()
    }

    override fun dispose() {
        backButton.dispose()
    }
}