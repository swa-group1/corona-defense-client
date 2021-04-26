package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.TowerData
import com.coronadefense.states.GameObserver
import com.coronadefense.states.menuStates.LobbyState
import com.coronadefense.states.menuStates.MainMenuState
import com.coronadefense.utils.Constants.EASY_PRICE_MODIFIER
import com.coronadefense.utils.Textures
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.HARD_PRICE_MODIFIER
import com.coronadefense.utils.Constants.LEAVE_GAME_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.START_WAVE_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.SIDEBAR_SPACING
import com.coronadefense.utils.Constants.SHOP_TOWER_PADDING
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Constants.SMALL_ICON_SIZE
import com.coronadefense.utils.Constants.SMALL_ICON_SPACING
import com.coronadefense.utils.DIFFICULTY
import kotlinx.coroutines.*
import kotlin.math.ceil
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : PlayState(stateManager, gameObserver) {
  private val stageMap = Image()

  private val leftPositionX: Float = GAME_WIDTH - SIDEBAR_WIDTH * 0.75f
  private val rightPositionX: Float = GAME_WIDTH - SIDEBAR_WIDTH * 0.25f

  private val shopTitle = "SHOP"
  private val shopTitlePositionY: Float = GAME_HEIGHT - SIDEBAR_SPACING * 0.5f

  private val healthMoneyPositionY: Float = GAME_HEIGHT - SIDEBAR_SPACING * 1.5f

  private var towerList: List<TowerData>? = null
  private var selectedTower: Int? = null
  private var changeMode: Boolean = false
  private var towerPriceModifier: Float = when (gameObserver.difficulty) {
    DIFFICULTY.EASY -> EASY_PRICE_MODIFIER
    DIFFICULTY.HARD -> HARD_PRICE_MODIFIER
    else -> 1f
  }
  private val towerShopPositionsY: MutableList<Float> = mutableListOf()
  private var shopTowerSize = SIDEBAR_WIDTH * 0.5f - SHOP_TOWER_PADDING
  private val towerButtonSizeX: Float = SIDEBAR_WIDTH * 0.5f

  private var startWave: Boolean = false
  private val startWaveButtonText = "READY"
  private val startWavePositionY: Float = LEAVE_GAME_BUTTON_HEIGHT

  init {
    runBlocking {
      towerList = ApiClient.towerListRequest()

      val rows = ceil(towerList!!.size * 0.5f)
      val maxShopTowerSize = (
        GAME_HEIGHT - (SIDEBAR_SPACING + SHOP_TOWER_PADDING) * rows - SIDEBAR_SPACING * 2f - START_WAVE_BUTTON_HEIGHT - LEAVE_GAME_BUTTON_HEIGHT
      ) / rows
      if (maxShopTowerSize < shopTowerSize && maxShopTowerSize > 0) {
        shopTowerSize = maxShopTowerSize
      }

      for ((index, tower) in towerList!!.withIndex()) {
        val towerShopPositionY: Float = GAME_HEIGHT - SIDEBAR_SPACING * 2 - (shopTowerSize + SIDEBAR_SPACING + SHOP_TOWER_PADDING) * (1 + index / 2)
        towerShopPositionsY += towerShopPositionY

        val towerButton = Image()
        buttons += towerButton

        towerButton.setSize(towerButtonSizeX, shopTowerSize + SIDEBAR_SPACING + SHOP_TOWER_PADDING)
        towerButton.setPosition(
          (if (index % 2 == 0) leftPositionX else rightPositionX) - towerButtonSizeX * 0.5f,
          towerShopPositionY
        )

        towerButton.addListener(object : ClickListener() {
          override fun clicked(event: InputEvent?, x: Float, y: Float) {
            selectedTower = tower.TypeNumber
            changeMode = true

            println("placing tower: $selectedTower")
          }
        })

        stage.addActor(towerButton)
      }
    }

    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    val startWaveButton = Image()
    buttons += startWaveButton

    startWaveButton.setSize(SIDEBAR_WIDTH, START_WAVE_BUTTON_HEIGHT)
    startWaveButton.setPosition(GAME_WIDTH - SIDEBAR_WIDTH, startWavePositionY)

    startWaveButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        startWave = true
      }
    })

    stage.addActor(startWaveButton)
  }

  private fun shopMode() {
    stageMap.clearListeners()
  }

  private fun placementMode() {
    gameObserver.gameStage?.let {
      stageMap.addListener(object: ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          val cellPositionX = floor(x / gameObserver.gameStage!!.tileWidth).toInt()
          val cellPositionY = floor(y / gameObserver.gameStage!!.tileHeight).toInt()

          println("clicked ($cellPositionX, $cellPositionY)")

          selectedTower?.let {
            runBlocking {
              ApiClient.placeTowerRequest(
                gameObserver.lobbyId,
                gameObserver.accessToken,
                selectedTower!!,
                cellPositionX,
                cellPositionY
              )
              selectedTower = null
              changeMode = true
            }
          }
        }
      })
    }
  }

  override fun update(deltaTime: Float) {
    if (super.update()) {
      return
    }

    if (gameObserver.socketClosed) {
      stateManager.set(MainMenuState(stateManager))
      return
    }

    gameObserver.gameStage?.let {
      if (startWave) {
        runBlocking {
          ApiClient.startRoundRequest(gameObserver.lobbyId, gameObserver.accessToken)
        }
        startWave = false
      }
    }

    when (gameObserver.gameState) {
      "fight" -> stateManager.set(PlayStateWave(stateManager, gameObserver))
      "lobby" -> stateManager.set(LobbyState(stateManager, gameObserver))
    }

    if (changeMode) {
      if (selectedTower == null) {
        shopMode()
      } else {
        placementMode()
      }
      changeMode = false
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined

    sprites.begin()
    sprites.draw(
      Textures.stage(gameObserver.gameStage!!.Number),
      0f,
      0f,
      GAME_WIDTH - SIDEBAR_WIDTH,
      GAME_HEIGHT
    )
    super.renderSidebar(sprites)
    sprites.end()

    super.draw()

    sprites.begin()

    font.draw(
      sprites,
      shopTitle,
      centerPositionX - font.width(shopTitle) * 0.5f,
      shopTitlePositionY + font.height(shopTitle) * 0.5f
    )

    gameObserver.health?.let {
      sprites.draw(
        Textures.icon("heart"),
        leftPositionX - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
        healthMoneyPositionY - SMALL_ICON_SIZE * 0.5f,
        SMALL_ICON_SIZE,
        SMALL_ICON_SIZE
      )
      val healthText = gameObserver.health!!.toString()
      font.draw(
        sprites,
        healthText,
        leftPositionX + SMALL_ICON_SPACING - font.width(healthText) * 0.5f,
        healthMoneyPositionY + font.height(healthText) * 0.5f
      )
    }

    gameObserver.money?.let {
      sprites.draw(
        Textures.icon("money"),
        rightPositionX - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
        healthMoneyPositionY - SMALL_ICON_SIZE * 0.5f,
        SMALL_ICON_SIZE,
        SMALL_ICON_SIZE
      )
      val moneyText = gameObserver.money!!.toString()
      font.draw(
        sprites,
        moneyText,
        rightPositionX + SMALL_ICON_SPACING - font.width(moneyText) * 0.5f,
        healthMoneyPositionY + font.height(moneyText) * 0.5f
      )
    }

    towerList?.let {
      for ((index, tower) in towerList!!.withIndex()) {
        val positionX = if (index % 2 == 0) leftPositionX else rightPositionX
        sprites.draw(
          Textures.tower(tower.TypeNumber),
          positionX - shopTowerSize * 0.5f,
          towerShopPositionsY[index] + SIDEBAR_SPACING,
          shopTowerSize,
          shopTowerSize
        )
        sprites.draw(
          if (tower.TypeNumber == selectedTower) Textures.button("standard") else Textures.button("gray"),
          positionX - towerButtonSizeX * 0.5f,
          towerShopPositionsY[index],
          towerButtonSizeX,
          shopTowerSize + SIDEBAR_SPACING + SHOP_TOWER_PADDING
        )
        sprites.draw(
          Textures.icon("money"),
          positionX - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
          towerShopPositionsY[index] + SIDEBAR_SPACING * 0.5f - SMALL_ICON_SIZE * 0.5f,
          SMALL_ICON_SIZE,
          SMALL_ICON_SIZE
        )
        val towerPriceText = (tower.MediumCost * towerPriceModifier).toInt().toString()
        font.draw(
          sprites,
          towerPriceText,
          positionX + SMALL_ICON_SPACING - font.width(towerPriceText) * 0.5f,
          towerShopPositionsY[index] + SIDEBAR_SPACING * 0.5f + font.height(towerPriceText) * 0.5f
        )
      }
    }

    sprites.draw(
      Textures.button("standard"),
      GAME_WIDTH - SIDEBAR_WIDTH,
      startWavePositionY,
      SIDEBAR_WIDTH,
      START_WAVE_BUTTON_HEIGHT
    )
    font.draw(
      sprites,
      startWaveButtonText,
      centerPositionX - font.width(startWaveButtonText) / 2,
      startWavePositionY + (START_WAVE_BUTTON_HEIGHT + font.height(startWaveButtonText)) * 0.5f
    )

    // copies placedTowers list to avoid ConcurrentModificationException
    val currentPlacedTowers = gameObserver.placedTowers.toList()
    for (tower in currentPlacedTowers) {
      sprites.draw(
        Textures.tower(tower.type),
        tower.positionX * gameObserver.gameStage!!.tileWidth,
        tower.positionY * gameObserver.gameStage!!.tileHeight,
        gameObserver.gameStage!!.tileWidth,
        gameObserver.gameStage!!.tileHeight
      )
    }

    sprites.end()
  }

  override fun dispose() {
    super.dispose()
    stageMap.clearListeners()

    println("PlayStatePlacement disposed")
  }
}