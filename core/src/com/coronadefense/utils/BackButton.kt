package com.coronadefense.utils

import com.badlogic.gdx.graphics.Texture
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
  val texture = Texture(Textures.button("back"))
  private val button = Image(texture)
  var action: String? = null
  init {
    button.setSize(BACK_BUTTON_SIZE, BACK_BUTTON_SIZE)
    button.setPosition(BACK_BUTTON_X_OFFSET, GAME_HEIGHT - BACK_BUTTON_SIZE / 2 - BACK_BUTTON_Y_OFFSET)
    button.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        action = actionToSet
      }
    })
    stage.addActor(button)
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
    texture.dispose()
    button.clearListeners()
  }
}