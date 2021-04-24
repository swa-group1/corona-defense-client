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
import com.coronadefense.api.StagesData
import com.coronadefense.states.GameObserver
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.PlayStatePlacement
import com.coronadefense.utils.*
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import kotlinx.coroutines.*

class LobbyState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
): ObserverState(stateManager)  {
  private val background: Texture = Texture(Textures.background("menu"))
  private val font = Font(20)
  private var difficultyNumber = 0
  private var gameStageNumber = 0
  private val xPositionDifficulty: Float = GAME_WIDTH / 2 - LIST_ITEM_WIDTH / 4 - Constants.SIDEBAR_WIDTH/2
  private val xPositionGameStage: Float = GAME_WIDTH / 2 - LIST_ITEM_WIDTH / 4 + Constants.SIDEBAR_WIDTH/2
  private var gameStages:List<SimpleStageData>? = null
  private var gameStageSelectDisplayed = false

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    GlobalScope.launch {
      gameStages = ApiClient.stagesListRequest()
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
          ApiClient.startGameRequest(gameObserver.lobbyId, gameObserver.accessToken, gameStageNumber, difficultyNumber)
        }
      }
    })
    stage.addActor(startGameButton)
    addDifficultySelectButtons()
  }

  private fun addDifficultySelectButtons(){
    for (difficulty in Difficulty.values()) {
      val yPosition: Float =
              (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (difficulty.value + 2))
      val difficultyTexture = Texture(Textures.button("gray"))
      textures += difficultyTexture

      val difficultyButton = Image(difficultyTexture)
      buttons += difficultyButton
      difficultyButton.setSize(LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
      difficultyButton.setPosition(xPositionDifficulty, yPosition)

      difficultyButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          difficultyNumber = difficulty.value
          println(difficulty.name)
        }
      })
      stage.addActor(difficultyButton)
    }
  }

  private fun displayDifficultySelectText(sprites: SpriteBatch){
    for (difficulty in Difficulty.values()) {
      val yPosition: Float =
              (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (difficulty.value + 2))
      font.draw(
              sprites,
              difficulty.name,
              xPositionDifficulty + (LIST_ITEM_WIDTH/2-font.width(difficulty.name))/2,
              yPosition +(Constants.LIST_ITEM_HEIGHT+font.height(difficulty.name))/2
      )
    }
    val difficultySelected = Texture(Textures.button("standard"))
    textures += difficultySelected
    sprites.draw(difficultySelected, xPositionDifficulty, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (difficultyNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
    sprites.draw(difficultySelected, xPositionDifficulty, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (difficultyNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
    sprites.draw(difficultySelected, xPositionDifficulty, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (difficultyNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)

  }

  private fun addStageSelectButtons(){
    for (gameStage in gameStages!!) {
      val yPosition: Float =
              (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (gameStage.Number + 2))
      val stageSelectTexture = Texture(Textures.button("gray"))
      textures += stageSelectTexture

      val stageSelectButton = Image(stageSelectTexture)
      buttons += stageSelectButton
      stageSelectButton.setSize(LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
      stageSelectButton.setPosition(xPositionGameStage, yPosition)

      stageSelectButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          gameStageNumber = gameStage.Number
          println(gameStage.Name)
        }
      })
      stage.addActor(stageSelectButton)
    }
    gameStageSelectDisplayed = true
  }

  private fun displayStageSelectText(sprites: SpriteBatch){
    for (gameStage in gameStages!!) {
      val yPosition: Float =
              (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (gameStage.Number + 2))
      font.draw(
              sprites,
              gameStage.Name,
              xPositionGameStage + (LIST_ITEM_WIDTH/2-font.width(gameStage.Name))/2,
              yPosition +(Constants.LIST_ITEM_HEIGHT+font.height(gameStage.Name))/2
      )
    }
    val stageSelected = Texture(Textures.button("standard"))
    textures += stageSelected
    sprites.draw(stageSelected, xPositionGameStage, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (gameStageNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
    sprites.draw(stageSelected, xPositionGameStage, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (gameStageNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)
    sprites.draw(stageSelected, xPositionGameStage, (GAME_HEIGHT / 2) - ((Constants.LIST_ITEM_HEIGHT + Constants.LIST_ITEM_SPACING) * (gameStageNumber + 2)), LIST_ITEM_WIDTH/2, Constants.LIST_ITEM_HEIGHT)

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
    displayDifficultySelectText(sprites)

    if(gameStages != null){
      displayStageSelectText(sprites)
      if(!gameStageSelectDisplayed){
        addStageSelectButtons()
      }
    }
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