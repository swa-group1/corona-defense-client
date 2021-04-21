package com.coronadefense.states.lobby

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.types.Lobby
import com.coronadefense.states.State
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.MenuState
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.inputRound.PlayStatePlacement
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Font
import kotlinx.coroutines.*

class LobbyState(
  stateManager: GameStateManager,
  val lobby: Lobby
): ObserverState(stateManager)  {
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background: Texture = Texture("initiate_game_state.jpg")
  private val font: BitmapFont = Font.generateFont(20)

  private var gameStartData: Int? = null

  val startGameTexture = Texture("greenBorder.png")
  val startGameButton = Image(startGameTexture)

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    startGameButton.setSize(180f, 60f)
    startGameButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-210)
    startGameButton.addListener(object: ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        GlobalScope.launch {
          ApiClient.startGameRequest(lobby.id, lobby.accessToken, 0, 0)
          ApiClient.close()
        }
      }
    })
    stage.addActor(startGameButton)
  }

  val backButton = BackButton("LeaveLobby", stateManager, stage, lobby)

  override fun handleGameModeMessage(message: GameModeMessage) {
    gameStartData = message.stageNumber
  }

  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {
    lobby.playerCount++
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    val xPosition: Float = Game.WIDTH / 2 - 150f
    font.draw(sprites, "LOBBY: ${lobby.name}", xPosition, Game.HEIGHT/2+70)
    for (playerIndex in 0 until lobby.playerCount) {
      val yPosition: Float = (Game.HEIGHT / 2) + 40f - (30f * playerIndex)
      font.draw(sprites, "Player ${playerIndex + 1}", xPosition, yPosition)
    }
    font.draw(sprites, "START GAME", Game.WIDTH/2-60, Game.HEIGHT/2-175)
    sprites.end()
    stage.draw()
  }

  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    background.dispose()
    font.dispose()

    backButton.dispose()

    startGameTexture.dispose()
    startGameButton.clearListeners()

    println("LobbyState disposed")
  }

  override fun handleInput() {}
  override fun update(deltaTime: Float) {
    backButton.update()
    gameStartData?.let {
      val playState = PlayStatePlacement(stateManager, lobby, gameStartData!!)
      Game.receiver.addObserver(playState)
      Game.receiver.removeObserver(this)
      stateManager.set(playState)
    }
  }
}