package com.coronadefense.states.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
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
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.utils.BackButton
import com.coronadefense.states.MenuState
import com.coronadefense.utils.Font

class LobbyListState(stateManager: GameStateManager): State(stateManager)  {
  var lobbyList: List<LobbyData>? = null
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    GlobalScope.launch {
      lobbyList = ApiClient.lobbyListRequest()
    }
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background = Texture("initiate_game_state.jpg")
  private val createLobbyButton = Image(Texture("greenBorder.png"))
  val font: BitmapFont = Font.generateFont(20)
  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    BackButton.addBackButton(stateManager, MenuState(stateManager), stage)
    createLobbyButton.setSize(180f, 60f)
    createLobbyButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-210)
    createLobbyButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        stateManager.set(CreateLobbyState(stateManager))
      }
    })
    stage.addActor(createLobbyButton)
  }
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
        val yPosition: Float = (Game.HEIGHT / 2) + 50f - (30f * lobbyIndex)
        font.draw(sprites, lobbyList!![lobbyIndex].name, xPosition, yPosition)
        font.draw(sprites, lobbyList!![lobbyIndex].playerCount.toString(), xPosition + 200f, yPosition)
      }
    }
    font.draw(sprites, "CREATE LOBBY", Game.WIDTH/2-70, Game.HEIGHT/2-175)
    sprites.end()
    stage.draw()
  }
  override fun dispose() {
  }

}