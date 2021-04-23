package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.Game
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.types.Lobby
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.PlayStatePlacement
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures
import kotlinx.coroutines.*

/**
 * State to show a lobby before starting a game.
 * @param stateManager Manager of all states.
 * @param lobby The lobby the player is currently in.
 */
class LobbyState(
  stateManager: StateManager,
  val lobby: Lobby
): ObserverState(stateManager)  {
  private val background: Texture = Texture(Textures.background("menu"))
  private val font = Font(20)

  private var gameStartData: Int? = null

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    val startGameTexture = Texture(Textures.button("standard"))
    textures += startGameTexture

    val startGameButton = Image(startGameTexture)
    buttons += startGameButton

    startGameButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    startGameButton.setPosition(
      (GAME_WIDTH - MENU_BUTTON_WIDTH) / 2,
      GAME_HEIGHT / 2 + BOTTOM_BUTTON_OFFSET
    )

    startGameButton.addListener(object: ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        GlobalScope.launch {
          ApiClient.startGameRequest(lobby.id, lobby.accessToken, 2, 0)
        }
      }
    })

    stage.addActor(startGameButton)
  }

  private val backButton = BackButton("LeaveLobby", stateManager, stage, lobby)

  override fun handleGameModeMessage(message: GameModeMessage) {
    super.handleGameModeMessage(message)
    gameStartData = message.stageNumber
  }

  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {
    super.handlePlayerCountUpdateMessage(message)
    lobby.playerCount++
  }

  override fun update(deltaTime: Float) {
    backButton.update()

    gameStartData?.let {
      val playState = PlayStatePlacement(stateManager, lobby, gameStartData!!)
      Game.receiver.addObserver(playState)
      Game.receiver.removeObserver(this)
      stateManager.set(playState)
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    val lobbyTitle = "LOBBY: ${lobby.name}"
    font.draw(
      sprites,
      lobbyTitle,
      (GAME_WIDTH - font.width(lobbyTitle)) / 2,
      (GAME_HEIGHT - font.height(lobbyTitle)) / 2 + MENU_TITLE_OFFSET
    )

    val xPosition: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) / 2
    for (playerIndex in 0 until lobby.playerCount) {
      val yPosition: Float = (GAME_HEIGHT / 2) + 40f - (30f * playerIndex)

      font.draw(sprites, "Player ${playerIndex + 1}", xPosition, yPosition)
    }

    val startGameButtonText = "START GAME"
    font.draw(
      sprites,
      startGameButtonText,
      (GAME_WIDTH - font.width(startGameButtonText)) / 2,
      (GAME_HEIGHT + MENU_BUTTON_HEIGHT + font.height(startGameButtonText)) / 2 + BOTTOM_BUTTON_OFFSET
    )

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()

    background.dispose()
    font.dispose()
    backButton.dispose()

    println("LobbyState disposed")
  }
}