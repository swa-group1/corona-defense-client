package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.Game
import com.coronadefense.types.GameStage
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.states.GameObserver
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.utils.Textures
import com.coronadefense.types.*
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.types.utils.Position
import com.coronadefense.utils.Constants.BOTTOM_BUTTON_OFFSET
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.NUM_OF_TOWERS
import com.coronadefense.utils.Constants.SIDEBAR_TEXT_SPACING
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Font
import kotlinx.coroutines.*
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : ObserverState(stateManager) {
  private val font = Font(20)

  private val stageMapTexture: Texture = Texture(Textures.stage(gameObserver.stageNumber!!))
  private val stageMap = Image(stageMapTexture)

  init {
    runBlocking {
      gameObserver.gameStage = ApiClient.gameStageRequest(gameObserver.stageNumber!!)
    }
  }

  private var startWave: Boolean = false

  private var towerTypeToPlace: Int? = null
  private var changeMode: Boolean = false

  init {
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    val startWaveButtonTexture = Texture(Textures.button("standard"))
    textures += startWaveButtonTexture

    val startWaveButton = Image(startWaveButtonTexture)
    buttons += startWaveButton

    startWaveButton.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
    startWaveButton.setPosition(
      GAME_WIDTH - (SIDEBAR_WIDTH + MENU_BUTTON_WIDTH) / 2,
      GAME_HEIGHT / 2 + BOTTOM_BUTTON_OFFSET
    )

    startWaveButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        startWave = true
      }
    })

    stage.addActor(startWaveButton)

    val towerShopSize = SIDEBAR_WIDTH / 2
    val towerShopX: Float = GAME_WIDTH - SIDEBAR_WIDTH

    for (towerType in 0 until NUM_OF_TOWERS) {
      val towerShopY: Float =
        GAME_HEIGHT - SIDEBAR_TEXT_SPACING - (towerShopSize + SIDEBAR_TEXT_SPACING) * (1 + towerType / 2)

      val towerTexture = Texture(Textures.tower(towerType))
      textures += towerTexture

      val towerButton = Image(towerTexture)
      buttons += towerButton

      towerButton.setSize(towerShopSize, towerShopSize)
      towerButton.setPosition(towerShopX + towerType.rem(2) * towerShopSize, towerShopY)

      towerButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          towerTypeToPlace = towerType
          changeMode = true

          println("placing tower: $towerTypeToPlace")
        }
      })

      stage.addActor(towerButton)
    }
  }

  private fun shopMode() {
    stageMap.clearListeners()
  }

  private fun placementMode() {
    gameObserver.gameStage?.let {
      stageMap.addListener(object: ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          val cellPosition = Position(
            floor(x / gameObserver.gameStage!!.tileWidth).toInt(), floor(y / gameObserver.gameStage!!.tileHeight).toInt()
          )

          println("clicked x: ${cellPosition.x} y: ${cellPosition.y}")

          runBlocking {
            ApiClient.placeTowerRequest(gameObserver.lobbyId, gameObserver.accessToken, towerTypeToPlace!!, cellPosition.x, cellPosition.y)
            towerTypeToPlace = null
            changeMode = true
          }
        }
      })
    }
  }

  override fun update(deltaTime: Float) {
    gameObserver.gameStage?.let {
      if (startWave) {
        val playStateWave = PlayStateWave(stateManager, gameObserver)
        stateManager.set(playStateWave)
        Game.receiver.addObserver(playStateWave)
      }
    }

    if (changeMode) {
      if (towerTypeToPlace == null) {
        shopMode()
      } else {
        placementMode()
      }
      changeMode = false
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined

    super.draw()
    sprites.begin()

    val shopTitle = "SHOP"
    font.draw(
      sprites,
      shopTitle,
      GAME_WIDTH - (SIDEBAR_WIDTH + font.width(shopTitle)) / 2,
      GAME_HEIGHT - SIDEBAR_TEXT_SPACING + font.height(shopTitle) / 2
    )

    val startWaveButtonText1 = "RELEASE"
    val startWaveButtonText2 = "THE VIRUS"
    val startWaveButtonTextX = GAME_WIDTH - (SIDEBAR_WIDTH) / 2
    val startWaveButtonTextY = GAME_HEIGHT / 2 + BOTTOM_BUTTON_OFFSET + MENU_BUTTON_HEIGHT / 2
    font.draw(
      sprites,
      startWaveButtonText1,
      startWaveButtonTextX - font.width(startWaveButtonText1) / 2,
      startWaveButtonTextY + 5f + font.height(startWaveButtonText1)
    )
    font.draw(
      sprites,
      startWaveButtonText2,
      startWaveButtonTextX - font.width(startWaveButtonText2) / 2,
      startWaveButtonTextY - font.height(startWaveButtonText2) / 2,
    )

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
    super.dispose()

    font.dispose()

    stageMapTexture.dispose()
    stageMap.clearListeners()

    println("PlayStatePlacement disposed")
  }
}