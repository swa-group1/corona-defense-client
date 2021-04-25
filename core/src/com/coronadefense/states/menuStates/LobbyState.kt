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
import com.coronadefense.api.SimpleStageData
import com.coronadefense.states.GameObserver
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.PlayStatePlacement
import com.coronadefense.utils.*
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.LIST_ITEM_SPACING
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import kotlinx.coroutines.*
import kotlin.math.floor

class LobbyState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : ObserverState(stateManager) {
  private val background: Texture = Texture(Textures.background("menu"))
  private val font = Font(20)

  val selectedTexture = Texture(Textures.button("standard"))
  val notSelectedTexture = Texture(Textures.button("gray"))

  private var selectedDifficulty = 0
  private val xPositionDifficulty: Float = GAME_WIDTH / 2 - LIST_ITEM_WIDTH / 4 - Constants.SIDEBAR_WIDTH / 2

  private var gameStages: List<SimpleStageData>? = null
  private var selectedGameStage = 0
  private val xPositionGameStage: Float = GAME_WIDTH / 2 - LIST_ITEM_WIDTH / 4 + Constants.SIDEBAR_WIDTH / 2

  fun selectButtonY(index: Int): Float {
    return GAME_HEIGHT / 2 - (LIST_ITEM_HEIGHT + LIST_ITEM_SPACING) * (index + 2)
  }

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    GlobalScope.launch {
      gameStages = ApiClient.stagesListRequest()
      addStageSelectButtons()
    }

    addDifficultySelectButtons()

    val startGameTexture = Texture(Textures.button("standard"))
    textures += startGameTexture

    val startGameButton = Image(startGameTexture)
    buttons += startGameButton

    startGameButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    startGameButton.setPosition(
      (GAME_WIDTH - MENU_BUTTON_WIDTH) / 2,
      GAME_HEIGHT / 2 + BOTTOM_BUTTON_OFFSET
    )

    startGameButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        GlobalScope.launch {
          ApiClient.startGameRequest(gameObserver.lobbyId, gameObserver.accessToken, selectedGameStage, selectedDifficulty)
        }
      }
    })
    stage.addActor(startGameButton)
  }

  private fun addDifficultySelectButtons() {
    for (difficulty in Difficulty.values()) {
      val difficultyButton = Image()
      buttons += difficultyButton

      difficultyButton.setSize(LIST_ITEM_WIDTH / 2, LIST_ITEM_HEIGHT)
      difficultyButton.setPosition(xPositionDifficulty, selectButtonY(difficulty.value))

      difficultyButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          selectedDifficulty = difficulty.value
          println(difficulty.name)
        }
      })
      stage.addActor(difficultyButton)
    }
  }

  private fun renderDifficultySelect(sprites: SpriteBatch) {
    val difficultyTitle = "Mode"
    font.draw(
      sprites,
      difficultyTitle,
      xPositionDifficulty + (LIST_ITEM_WIDTH / 2 - font.width(difficultyTitle)) / 2,
      selectButtonY(0) + LIST_ITEM_HEIGHT * 3/2 + font.height(difficultyTitle) / 2
    )

    for (difficulty in Difficulty.values()) {
      sprites.draw(
        if (selectedDifficulty == difficulty.value) selectedTexture else notSelectedTexture,
        xPositionDifficulty,
        selectButtonY(difficulty.value),
        LIST_ITEM_WIDTH / 2,
        LIST_ITEM_HEIGHT
      )

      font.draw(
        sprites,
        difficulty.name,
        xPositionDifficulty + (LIST_ITEM_WIDTH / 2 - font.width(difficulty.name)) / 2,
        selectButtonY(difficulty.value) + (LIST_ITEM_HEIGHT + font.height(difficulty.name)) / 2
      )
    }
  }

  private fun addStageSelectButtons() {
    gameStages?.let {
      for (gameStage in gameStages!!) {
        val stageSelectButton = Image()
        buttons += stageSelectButton

        stageSelectButton.setSize(LIST_ITEM_WIDTH / 2, LIST_ITEM_HEIGHT)
        stageSelectButton.setPosition(xPositionGameStage, selectButtonY(gameStage.Number))

        stageSelectButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            selectedGameStage = gameStage.Number
            println(gameStage.Name)
          }
        })
        stage.addActor(stageSelectButton)
      }
    }
  }

  private fun renderStageSelect(sprites: SpriteBatch) {
    gameStages?.let {
      val stageTitle = "Map"
      font.draw(
        sprites,
        stageTitle,
        xPositionGameStage + (LIST_ITEM_WIDTH / 2 - font.width(stageTitle)) / 2,
        selectButtonY(0) + LIST_ITEM_HEIGHT * 3/2 + font.height(stageTitle) / 2
      )

      for (gameStage in gameStages!!) {
        sprites.draw(
          if (selectedGameStage == gameStage.Number) selectedTexture else notSelectedTexture,
          xPositionGameStage,
          selectButtonY(gameStage.Number),
          LIST_ITEM_WIDTH / 2,
          LIST_ITEM_HEIGHT
        )

        font.draw(
          sprites,
          gameStage.Name,
          xPositionGameStage + (LIST_ITEM_WIDTH / 2 - font.width(gameStage.Name)) / 2,
          selectButtonY(gameStage.Number) + (LIST_ITEM_HEIGHT + font.height(gameStage.Name)) / 2
        )
      }
    }
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

    for (playerIndex in 0 until gameObserver.playerCount) {
      val playerText = "Player ${playerIndex + 1}"
      font.draw(
        sprites,
        playerText,
        (if (playerIndex % 2 == 0) xPositionDifficulty else xPositionGameStage) + (LIST_ITEM_WIDTH / 2 - font.width(playerText)) / 2,
        selectButtonY(playerIndex / 2) + LIST_ITEM_HEIGHT * 5
      )
    }

    val startGameButtonText = "START GAME"
    font.draw(
      sprites,
      startGameButtonText,
      (GAME_WIDTH - font.width(startGameButtonText)) / 2,
      (GAME_HEIGHT + MENU_BUTTON_HEIGHT + font.height(startGameButtonText)) / 2 + BOTTOM_BUTTON_OFFSET
    )

    renderDifficultySelect(sprites)
    renderStageSelect(sprites)

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()

    background.dispose()
    font.dispose()
    backButton.dispose()
    selectedTexture.dispose()
    notSelectedTexture.dispose()

    println("LobbyState disposed")
  }
}