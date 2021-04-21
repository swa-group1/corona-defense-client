package com.coronadefense.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.states.MenuState
import com.coronadefense.states.State
import com.coronadefense.states.lobby.LobbyListState
import com.coronadefense.types.Lobby
import kotlinx.coroutines.*

class BackButton(
  private val actionToSet: String,
  private val stateManager: GameStateManager,
  stage: Stage,
  private val lobby: Lobby? = null,
) {
  val texture = Texture("backbutton.png")
  val button = Image(texture)
  var action: String? = null
  init {
    button.setSize(100f, 100f)
    button.setPosition(Game.WIDTH/2-350, Game.HEIGHT/2+130)
    button.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        action = actionToSet
      }
    })
    stage.addActor(button)
  }
  fun update() {
    when (action) {
      "MainMenu" -> stateManager.set(MenuState(stateManager))
      "LeaveLobby" -> {
        lobby?.let {
          GlobalScope.launch {
            ApiClient.leaveLobbyRequest(lobby.id, lobby.accessToken)
          }
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