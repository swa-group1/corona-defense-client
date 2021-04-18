package com.coronadefense.states.lobby

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.LobbyData
import com.coronadefense.states.State
import kotlinx.coroutines.*

class LobbyListState(stateManager: GameStateManager): State(stateManager)  {
  var lobbyList: List<LobbyData>? = null
  val font: BitmapFont = BitmapFont()
  private val background = Texture("initiate_game_state.jpg")
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    GlobalScope.launch {
      lobbyList = ApiClient.lobbyListRequest()
    }
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  override fun handleInput() {
  }
  override fun update(deltaTime: Float) {
  }
  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    lobbyList?.let {
      val xPosition: Float = Game.WIDTH / 2 - 150f
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Game.HEIGHT / 2) + 20f - (30f * lobbyIndex)
        font.draw(sprites, lobbyList!![lobbyIndex].name, xPosition, yPosition)
        font.draw(sprites, lobbyList!![lobbyIndex].playerCount.toString(), xPosition + 100f, yPosition)
      }
    }
    sprites.end()
    stage.draw()
  }
  override fun dispose() {
  }

}