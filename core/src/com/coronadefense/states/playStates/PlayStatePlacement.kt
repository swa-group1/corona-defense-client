package com.coronadefense.states.playStates

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
import com.coronadefense.GameStage
import com.coronadefense.states.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.utils.Textures
import com.coronadefense.types.*
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.types.utils.Position
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font
import kotlinx.coroutines.*
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: GameStateManager,
  val lobby: Lobby,
  stageNumber: Int
) : ObserverState(stateManager) {
  init {
    camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(
    Constants.GAME_WIDTH,
    Constants.GAME_HEIGHT,
    camera
  )
  private val stage: Stage = Stage(viewport, Game.sprites)

  private val stageMapTexture: Texture = Texture(Textures.stage(stageNumber))
  private val stageMap = Image(stageMapTexture)

  private val font: BitmapFont = Font.generateFont(20)

  private var gameStage: GameStage? = null
  init {
    runBlocking {
      gameStage = ApiClient.gameStageRequest(stageNumber)
    }
  }

  private var towerTypeToPlace: Int? = null
  private var changeMode: Boolean = false

  private val towerTextures: MutableList<Texture> = mutableListOf()
  private val towerButtons: MutableList<Image> = mutableListOf()

  private val placedTowers: MutableList<Tower> = mutableListOf()

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    stageMap.setSize(Constants.GAME_WIDTH - Constants.SIDEBAR_WIDTH, Constants.GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    val towerSize = 100f
    val towerShopX: Float = Constants.GAME_WIDTH / 2 + 250

    for (towerType in 0 until Constants.NUM_OF_TOWERS) {
      val towerShopY: Float = (Constants.GAME_HEIGHT / 2) + 100f - ((towerSize) * towerType)

      val towerTexture = Texture(Textures.tower(towerType))
      towerTextures += towerTexture

      val towerButton = Image(towerTexture)
      towerButton.setSize(towerSize, towerSize)
      towerButton.setPosition(towerShopX, towerShopY)
      towerButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          towerTypeToPlace = towerType.toInt()
          println("placing tower: $towerTypeToPlace")
          changeMode = true
        }
      })
      towerButtons += towerButton

      stage.addActor(towerButton)
    }
  }

  fun normalMode() {
    stageMap.clearListeners()
  }

  fun placeTowerMode() {
    gameStage?.let {
      stageMap.addListener(object: ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          val cellPosition = Position(
            floor(x / gameStage!!.tileWidth).toInt(), floor(y / gameStage!!.tileHeight).toInt()
          )
          println("clicked x: ${cellPosition.x} y: ${cellPosition.y}")
          runBlocking {
            ApiClient.placeTowerRequest(lobby.id, lobby.accessToken, towerTypeToPlace!!, cellPosition.x, cellPosition.y)
            towerTypeToPlace = null
            changeMode = true
          }
        }
      })

      for (xPosition in 0 until gameStage!!.XSize) {
        for (yPosition in 0 until gameStage!!.YSize) {

        }
      }
    }
  }

  override fun handleTowerPositionMessage(message: TowerPositionMessage) {
    super.handleTowerPositionMessage(message)
    placedTowers += Tower(
      message.towerId,
      message.typeNumber,
      Position(message.xPosition, message.yPosition)
    )
  }

  override fun update(deltaTime: Float) {
    if (changeMode) {
      if (towerTypeToPlace == null) {
        normalMode()
      } else {
        placeTowerMode()
      }
      changeMode = false
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    stage.draw()
    sprites.begin()
    font.draw(sprites, "SHOP", Constants.GAME_WIDTH / 2 + 240, Constants.GAME_HEIGHT / 2 + 220)

    // copies placedTowers list to avoid ConcurrentModificationException
    val currentPlacedTowers = placedTowers.toList()
    gameStage?.let {
      for (tower in currentPlacedTowers) {
        sprites.draw(
          Texture(Textures.tower(tower.type)),
          tower.position.x * gameStage!!.tileWidth,
          tower.position.y * gameStage!!.tileHeight,
          gameStage!!.tileWidth,
          gameStage!!.tileHeight
        )
      }
    }
    sprites.end()
  }

  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    font.dispose()

    stageMapTexture.dispose()
    stageMap.clearListeners()

    for (texture in towerTextures) {
      texture.dispose()
    }
    for (button in towerButtons) {
      button.clearListeners()
    }

    println("PlayStatePlacement disposed")
  }
}