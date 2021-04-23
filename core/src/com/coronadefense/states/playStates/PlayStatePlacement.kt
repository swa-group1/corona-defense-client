package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.types.GameStage
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.utils.Textures
import com.coronadefense.types.*
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.types.utils.Position
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.NUM_OF_TOWERS
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Font
import kotlinx.coroutines.*
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: StateManager,
  private val lobby: Lobby,
  stageNumber: Int
) : ObserverState(stateManager) {
  private val font = Font(20)

  private val stageMapTexture: Texture = Texture(Textures.stage(stageNumber))
  private val stageMap = Image(stageMapTexture)

  private var gameStage: GameStage? = null
  init {
    runBlocking {
      gameStage = ApiClient.gameStageRequest(stageNumber)
    }
  }

  private var towerTypeToPlace: Int? = null
  private var changeMode: Boolean = false

  private val placedTowers: MutableList<Tower> = mutableListOf()

  init {
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    val towerShopSize = 100f
    val towerShopX: Float = GAME_WIDTH / 2 + 250

    for (towerType in 0 until NUM_OF_TOWERS) {
      val towerShopY: Float = (GAME_HEIGHT / 2) + 100f - ((towerShopSize) * towerType)

      val towerTexture = Texture(Textures.tower(towerType))
      textures += towerTexture

      val towerButton = Image(towerTexture)
      buttons += towerButton

      towerButton.setSize(towerShopSize, towerShopSize)
      towerButton.setPosition(towerShopX, towerShopY)
      towerButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          towerTypeToPlace = towerType.toInt()
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

    font.draw(sprites, "SHOP", GAME_WIDTH / 2 + 240, GAME_HEIGHT / 2 + 220)

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