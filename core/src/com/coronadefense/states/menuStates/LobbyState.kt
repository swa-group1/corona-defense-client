package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.SimpleStageData
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.states.playStates.PlayStatePlacement
import com.coronadefense.utils.*
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.COLUMN_ITEM_WIDTH
import com.coronadefense.utils.Constants.COLUMN_SPACING
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import kotlinx.coroutines.*

/**
 * State to set up a game and wait for other players.
 * Extends InputState to allow selection of game settings and to start the game.
 * @param stateManager Manager of all game states.
 * @param gameObserver Observes the Receiver for game messages, such as when to start the game.
 */
class LobbyState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  private val backButton = BackButton("LeaveLobby", stateManager, stage, gameObserver)

  private val font = Font(20)

  private var gameStages: List<SimpleStageData>? = null
  private var selectedGameStage = 0
  private var selectedDifficulty = 0

  // Utility function for the vertical positioning of elements in this state, to ensure uniform spacing.
  private fun positionY(listOffset: Int): Float {
    return GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - LIST_ITEM_HEIGHT * (listOffset + 0.5f)
  }

  private val centerPositionX: Float = GAME_WIDTH * 0.5f
  private val leftPositionX: Float = centerPositionX - COLUMN_SPACING
  private val rightPositionX: Float = centerPositionX + COLUMN_SPACING

  private val title = "LOBBY: ${gameObserver.lobbyName}"
  private val titlePositionY = GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET

  private val playerPositionsY: MutableList<Float> = mutableListOf()

  private val selectionTitlePositionY = positionY(5) + LIST_ITEM_HEIGHT * 0.5f

  private val difficultyTitle = "Mode"
  private val difficultyPositionsY: MutableList<Float> = mutableListOf()

  private val gameStageTitle = "Map"
  private val gameStagePositionsY: MutableList<Float> = mutableListOf()

  private val startGameButtonText = "START GAME"
  private val startGamePositionY = GAME_HEIGHT * 0.5f + BOTTOM_BUTTON_OFFSET

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    // Launches a coroutine to fetch the configured game stages (maps) from Firebase.
    // Once fetched, adds buttons to select each map.
    GlobalScope.launch {
      gameStages = ApiClient.stagesListRequest()
      for ((index, gameStage) in gameStages!!.withIndex()) {
        val stageSelectButton = Image()
        buttons += stageSelectButton

        val gameStagePositionY = positionY(index + 6)
        gameStagePositionsY += gameStagePositionY

        stageSelectButton.setSize(COLUMN_ITEM_WIDTH, LIST_ITEM_HEIGHT)
        stageSelectButton.setPosition(rightPositionX - COLUMN_ITEM_WIDTH * 0.5f, gameStagePositionY)

        stageSelectButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            selectedGameStage = gameStage.Number
          }
        })
        stage.addActor(stageSelectButton)
      }
    }

    // Adds buttons to select game difficulty.
    for ((index, difficulty) in DIFFICULTY.values().withIndex()) {
      val difficultyButton = Image()
      buttons += difficultyButton

      val difficultyPositionY = positionY(index + 6)
      difficultyPositionsY += difficultyPositionY

      difficultyButton.setSize(COLUMN_ITEM_WIDTH, LIST_ITEM_HEIGHT)
      difficultyButton.setPosition(leftPositionX - COLUMN_ITEM_WIDTH * 0.5f, difficultyPositionY)

      difficultyButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          selectedDifficulty = difficulty.value
        }
      })
      stage.addActor(difficultyButton)
    }

    val startGameButton = Image()
    buttons += startGameButton

    startGameButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    startGameButton.setPosition(
      centerPositionX - MENU_BUTTON_WIDTH * 0.5f,
      startGamePositionY
    )

    // Once Start Game button is clicked, send a request to the API to start the game with the user's current selections.
    startGameButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        GlobalScope.launch {
          ApiClient.startGameRequest(gameObserver.lobbyId, gameObserver.accessToken, selectedGameStage, selectedDifficulty)
        }
      }
    })
    stage.addActor(startGameButton)
  }

  override fun update(deltaTime: Float) {
    backButton.update()

    // If the lobby times out, return to lobby list.
    if (gameObserver.socketClosed) {
      stateManager.set(LobbyListState(stateManager))
      return
    }

    // If the GameObserver has a gameStage, then a startGameRequest has been sent, and the PlayState should begin.
    gameObserver.gameStage?.let {
      stateManager.set(PlayStatePlacement(stateManager, gameObserver))
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(Textures.background("menu"), 0F, 0F, GAME_WIDTH, GAME_HEIGHT)
    backButton.render(sprites)

    font.draw(
      sprites,
      title,
      centerPositionX - font.width(title) * 0.5f,
      titlePositionY + font.height(title) * 0.5f
    )

    // Renders "Player X" text for each player in the lobby.
    // Since the GameObserver is updated when new players join, this will also update on that.
    for (playerIndex in 0 until gameObserver.playerCount) {
      if (playerPositionsY.size <= playerIndex) {
        playerPositionsY += positionY(playerIndex / 2 + 1)
      }
      val playerText = "Player ${playerIndex + 1}"
      font.draw(
        sprites,
        playerText,
        (if (playerIndex % 2 == 0) leftPositionX else rightPositionX) - font.width(playerText) * 0.5f,
        playerPositionsY[playerIndex] + (LIST_ITEM_HEIGHT + font.height(playerText)) * 0.5f
      )
    }

    font.draw(
      sprites,
      difficultyTitle,
      leftPositionX - font.width(difficultyTitle) * 0.5f,
      selectionTitlePositionY + font.height(difficultyTitle) * 0.5f
    )

    // Renders text and textures for the difficulty buttons.
    // Renders different texture based on whether the difficulty is selected or not.
    for ((index, difficulty) in DIFFICULTY.values().withIndex()) {
      sprites.draw(
        if (selectedDifficulty == difficulty.value) Textures.button("standard") else Textures.button("gray"),
        leftPositionX - COLUMN_ITEM_WIDTH * 0.5f,
        difficultyPositionsY[index],
        COLUMN_ITEM_WIDTH,
        LIST_ITEM_HEIGHT
      )

      font.draw(
        sprites,
        difficulty.name,
        leftPositionX - font.width(difficulty.name) * 0.5f,
        difficultyPositionsY[index] + (LIST_ITEM_HEIGHT + font.height(difficulty.name)) * 0.5f
      )
    }

    // Checkes for fetched game stages, then renders button textures and text for them.
    gameStages?.let {
      font.draw(
        sprites,
        gameStageTitle,
        rightPositionX - font.width(gameStageTitle) * 0.5f,
        selectionTitlePositionY + font.height(gameStageTitle) * 0.5f
      )

      for ((index, gameStage) in gameStages!!.withIndex()) {
        if (index < gameStagePositionsY.size) {
          sprites.draw(
            if (selectedGameStage == gameStage.Number) Textures.button("standard") else Textures.button("gray"),
            rightPositionX - COLUMN_ITEM_WIDTH * 0.5f,
            gameStagePositionsY[index],
            COLUMN_ITEM_WIDTH,
            LIST_ITEM_HEIGHT
          )

          font.draw(
            sprites,
            gameStage.Name,
            rightPositionX - font.width(gameStage.Name) * 0.5f,
            gameStagePositionsY[index] + (LIST_ITEM_HEIGHT + font.height(gameStage.Name)) * 0.5f
          )
        }
      }
    }

    // Renders texture and text for the Start Game button.
    sprites.draw(
      Textures.button("standard"),
      centerPositionX - MENU_BUTTON_WIDTH * 0.5f,
      startGamePositionY,
      MENU_BUTTON_WIDTH,
      MENU_BUTTON_HEIGHT
    )
    font.draw(
      sprites,
      startGameButtonText,
      centerPositionX - font.width(startGameButtonText) * 0.5f,
      startGamePositionY + (MENU_BUTTON_HEIGHT + font.height(startGameButtonText)) * 0.5f
    )

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    font.dispose()
    backButton.dispose()

    println("LobbyState disposed")
  }
}