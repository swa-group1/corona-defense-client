package com.coronadefense.utils

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.GameObserver
import com.coronadefense.states.StateManager
import com.coronadefense.states.menuStates.MainMenuState
import com.coronadefense.states.menuStates.LobbyListState
import com.coronadefense.utils.Constants.BACK_BUTTON_SIZE
import com.coronadefense.utils.Constants.BACK_BUTTON_X_OFFSET
import com.coronadefense.utils.Constants.BACK_BUTTON_Y_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT

class BackButton(
  private val actionToSet: String,
  private val stateManager: StateManager,
  stage: Stage,
  private val gameObserver: GameObserver? = null,
) {
  private val button = Image()

  private val backButtonPositionY = GAME_HEIGHT - BACK_BUTTON_SIZE * 0.5f - BACK_BUTTON_Y_OFFSET

  var action: String? = null

  init {
    button.setSize(BACK_BUTTON_SIZE, BACK_BUTTON_SIZE)
    button.setPosition(BACK_BUTTON_X_OFFSET, backButtonPositionY)

    button.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        action = actionToSet
      }
    })
    stage.addActor(button)
  }

  fun render(sprites: SpriteBatch) {
    sprites.draw(
      Textures.button("back"),
      BACK_BUTTON_X_OFFSET,
      backButtonPositionY,
      BACK_BUTTON_SIZE,
      BACK_BUTTON_SIZE
    )
  }

  fun update() {
    when (action) {
      "MainMenu" -> stateManager.set(MainMenuState(stateManager))
      "LeaveLobby" -> {
        gameObserver?.let {
          gameObserver.leaveLobby()
        }
        stateManager.set(LobbyListState(stateManager))
      }
    }
  }

  fun dispose() {
    button.clearListeners()
    // Texture disposal handled by Textures
  }
}