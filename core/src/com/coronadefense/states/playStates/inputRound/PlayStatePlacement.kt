package com.coronadefense.states.playStates.inputRound

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
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.states.playStates.Textures
import com.coronadefense.types.*
import com.coronadefense.types.Constants.TOWER_TYPES
import com.coronadefense.utils.Font
import kotlinx.coroutines.*
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: GameStateManager,
  val lobby: Lobby,
  stageNumber: Int
) : ObserverState(stateManager) {
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)

  private val stageMapTexture: Texture = Texture(Textures.stagePath(stageNumber))
  private val stageMap = Image(stageMapTexture)

  private val font: BitmapFont = Font.generateFont(20)

  private var gameStage: GameStage? = null
  init {
    runBlocking {
      gameStage = ApiClient.gameStageRequest(stageNumber)
    }
    println("Game stage: name ${gameStage?.Name} x ${gameStage?.XSize}, y ${gameStage?.YSize}")
  }

  private val shopWidth: Float = Game.WIDTH / 4
  private val cellWidth = (Game.WIDTH - shopWidth) / gameStage!!.XSize
  private val cellHeight = Game.HEIGHT / gameStage!!.YSize

  private var towerTypeToPlace: Int? = null
  private var changeMode: Boolean = false

  private val towerTextures: MutableList<Texture> = mutableListOf()
  private val towerButtons: MutableList<Image> = mutableListOf()

  private val placedTowers: MutableList<Tower> = mutableListOf()

  private val placementButtons: MutableList<Image> = mutableListOf()

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    stageMap.setSize(Game.WIDTH - shopWidth, Game.HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    val towerSize = 100f
    val towerShopX: Float = Game.WIDTH / 2 + 250

    var towerIndex = 0
    for (towerType in Constants.TOWER_TYPES) {
      val towerShopY: Float = (Game.HEIGHT / 2) + 100f - ((towerSize) * towerIndex)
      towerIndex++

      val towerTexture = Texture(Textures.towerPath(towerType))
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
          val cellPosition = Position(floor(x / cellWidth).toInt(), floor(y / cellHeight).toInt())
          println("clicked x: ${cellPosition.x} y: ${cellPosition.y}")
          GlobalScope.launch {
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

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    stage.draw()
    sprites.begin()
    font.draw(sprites, "SHOP", Game.WIDTH / 2 + 270, Game.HEIGHT / 2 + 220)
    for (tower in placedTowers) {
      sprites.draw(
        Texture(Textures.towerPath(tower.type)),
        tower.position.x * cellWidth,
        tower.position.y * cellHeight,
        cellWidth,
        cellHeight
      )
    }
    sprites.end()
  }

  override fun handleInput() {}

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