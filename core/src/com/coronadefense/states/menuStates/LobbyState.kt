package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.states.GameObserver
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.PlayStatePlacement
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants
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

class LobbyState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
): ObserverState(stateManager)  {
  private val background: Texture = Texture(Textures.background("menu"))
  private val font = Font(20)

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
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
          ApiClient.startGameRequest(gameObserver.lobbyId, gameObserver.accessToken, 2, 0)
        }
      }
    })

    stage.addActor(startGameButton)
  }

  private val backButton = BackButton("LeaveLobby", stateManager, stage, gameObserver)

  override fun update(deltaTime: Float) {
    backButton.update()

    gameObserver.gameStage?.let {
      stateManager.set(PlayStatePlacement(stateManager, gameObserver))
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    val lobbyTitle = "LOBBY: ${gameObserver.lobbyName}"
    font.draw(
      sprites,
      lobbyTitle,
      (GAME_WIDTH - font.width(lobbyTitle)) / 2,
      (GAME_HEIGHT - font.height(lobbyTitle)) / 2 + MENU_TITLE_OFFSET
    )

    val xPosition: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) / 2
    for (playerIndex in 0 until gameObserver.playerCount) {
      val yPosition: Float =
        GAME_HEIGHT / 2 + MENU_TITLE_OFFSET - (Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (playerIndex + 1)

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