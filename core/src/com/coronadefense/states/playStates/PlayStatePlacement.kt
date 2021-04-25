package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.TowerData
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.states.menuStates.LobbyState
import com.coronadefense.types.utils.Coords
import com.coronadefense.utils.Textures
import com.coronadefense.types.utils.Position
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.SIDEBAR_SPACING
import com.coronadefense.utils.Constants.SHOP_TOWER_PADDING
import com.coronadefense.utils.Constants.SHOP_TOWER_SIZE
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Constants.SMALL_ICON_SIZE
import com.coronadefense.utils.Constants.SMALL_ICON_SPACING
import com.coronadefense.utils.Font
import kotlinx.coroutines.*
import kotlin.math.floor

class PlayStatePlacement(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  private val font = Font(20)

  private val sidebarTexture = Texture(Textures.background("sidebar"))
  private val heartTexture = Texture(Textures.icon("heart"))
  private val moneyTexture = Texture(Textures.icon("money"))
  private val selectedTexture = Texture(Textures.button("standard"))
  private val notSelectedTexture = Texture(Textures.button("gray"))

  private val stageMapTexture: Texture = Texture(Textures.stage(gameObserver.gameStage!!.Number))
  private val stageMap = Image(stageMapTexture)

  private var towerList: List<TowerData>? = null
  init {
    runBlocking {
      towerList = ApiClient.towerListRequest()
    }
  }
  private val towerShopTextures: MutableList<Texture> = mutableListOf()
  private val towerShopCoords: MutableList<Coords> = mutableListOf()
  private val towerButtonSizeX = SIDEBAR_WIDTH / 2
  private val towerButtonSizeY = SHOP_TOWER_SIZE + SIDEBAR_SPACING + SHOP_TOWER_PADDING

  private var startWave: Boolean = false

  private var selectedTower: Int? = null
  private var changeMode: Boolean = false

  init {
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)

    textures += heartTexture
    textures += moneyTexture

    val startWaveButtonTexture = Texture(Textures.button("standard"))
    textures += startWaveButtonTexture

    val startWaveButton = Image(startWaveButtonTexture)
    buttons += startWaveButton

    startWaveButton.setSize(
      SIDEBAR_WIDTH,
      GAME_HEIGHT - (SHOP_TOWER_SIZE + SIDEBAR_SPACING + SHOP_TOWER_PADDING) * 3 - 2 * SIDEBAR_SPACING
    )
    startWaveButton.setPosition(GAME_WIDTH - SIDEBAR_WIDTH, 0f)

    startWaveButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        startWave = true
      }
    })

    stage.addActor(startWaveButton)

    towerList?.let {
      for ((index, tower) in towerList!!.withIndex()) {
        towerShopTextures += Texture(Textures.tower(tower.TypeNumber))

        val towerShopX: Float = GAME_WIDTH - SIDEBAR_WIDTH * (3 - 2 * (index % 2)) * 0.25f
        val towerShopY: Float =
          GAME_HEIGHT - SIDEBAR_SPACING - towerButtonSizeY * (1 + index / 2)

        towerShopCoords += Coords(towerShopX, towerShopY)

        val towerButton = Image()
        buttons += towerButton

        towerButton.setSize(towerButtonSizeX, towerButtonSizeY)
        towerButton.setPosition(towerShopX - towerButtonSizeX / 2, towerShopY - SIDEBAR_SPACING)

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
  }

  private fun shopMode() {
    stageMap.clearListeners()
  }

  private fun placementMode() {
    gameObserver.gameStage?.let {
      stageMap.addListener(object: ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          val cellPosition = Position(
            floor(x / gameObserver.gameStage!!.tileWidth).toInt(),
            floor(y / gameObserver.gameStage!!.tileHeight).toInt()
          )

          println("clicked x: ${cellPosition.x} y: ${cellPosition.y}")

          selectedTower?.let {
            runBlocking {
              ApiClient.placeTowerRequest(
                gameObserver.lobbyId,
                gameObserver.accessToken,
                selectedTower!!,
                cellPosition.x,
                cellPosition.y
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
    sprites.draw(sidebarTexture, GAME_WIDTH - SIDEBAR_WIDTH, 0f, SIDEBAR_WIDTH, GAME_HEIGHT)
    sprites.end()

    super.draw()

    sprites.begin()

    val shopTitle = "SHOP"
    font.draw(
      sprites,
      shopTitle,
      GAME_WIDTH - (SIDEBAR_WIDTH + font.width(shopTitle)) / 2,
      GAME_HEIGHT - SIDEBAR_SPACING / 2 + font.height(shopTitle) / 2
    )

    gameObserver.health?.let {
      sprites.draw(
        heartTexture,
        GAME_WIDTH - (SIDEBAR_WIDTH) * 3/4 - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
        GAME_HEIGHT - SIDEBAR_SPACING * 3/2 - SMALL_ICON_SIZE / 2,
        SMALL_ICON_SIZE,
        SMALL_ICON_SIZE
      )
      val healthText = gameObserver.health!!.toString()
      font.draw(
        sprites,
        healthText,
        GAME_WIDTH - (SIDEBAR_WIDTH) * 3/4 - font.width(healthText) / 2 + SMALL_ICON_SPACING,
        GAME_HEIGHT - SIDEBAR_SPACING * 3/2 + font.height(healthText) / 2
      )
    }

    gameObserver.money?.let {
      sprites.draw(
        moneyTexture,
        GAME_WIDTH - (SIDEBAR_WIDTH) / 4 - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
        GAME_HEIGHT - SIDEBAR_SPACING * 3/2 - SMALL_ICON_SIZE / 2,
        SMALL_ICON_SIZE,
        SMALL_ICON_SIZE
      )
      val moneyText = gameObserver.money!!.toString()
      font.draw(
        sprites,
        moneyText,
        GAME_WIDTH - (SIDEBAR_WIDTH) / 4 - font.width(moneyText) / 2 + SMALL_ICON_SPACING,
        GAME_HEIGHT - SIDEBAR_SPACING * 3/2 + font.height(moneyText) / 2
      )
    }

    towerList?.let {
      for ((index, tower) in towerList!!.withIndex()) {
        sprites.draw(
          towerShopTextures[index],
          towerShopCoords[index].x - SHOP_TOWER_SIZE / 2,
          towerShopCoords[index].y,
          SHOP_TOWER_SIZE,
          SHOP_TOWER_SIZE
        )
        sprites.draw(
          if (tower.TypeNumber == selectedTower) selectedTexture else notSelectedTexture,
          towerShopCoords[index].x - towerButtonSizeX / 2,
          towerShopCoords[index].y - SIDEBAR_SPACING,
          towerButtonSizeX,
          towerButtonSizeY
        )
        sprites.draw(
          moneyTexture,
          towerShopCoords[index].x - SMALL_ICON_SIZE - SMALL_ICON_SPACING,
          towerShopCoords[index].y - SIDEBAR_SPACING / 2 - SMALL_ICON_SIZE / 2,
          SMALL_ICON_SIZE,
          SMALL_ICON_SIZE
        )
        val towerPriceText = tower.MediumCost.toString()
        font.draw(
          sprites,
          towerPriceText,
          towerShopCoords[index].x - font.width(towerPriceText) / 2 + SMALL_ICON_SPACING,
          towerShopCoords[index].y - SIDEBAR_SPACING / 2 + font.height(towerPriceText) / 2
        )
      }
    }

    val startWaveButtonText1 = "RELEASE"
    val startWaveButtonText2 = "THE VIRUS"
    val startWaveButtonTextX = GAME_WIDTH - (SIDEBAR_WIDTH) / 2
    val startWaveButtonTextY = (GAME_HEIGHT - (SHOP_TOWER_SIZE + SIDEBAR_SPACING + SHOP_TOWER_PADDING) * 3 - 2 * SIDEBAR_SPACING) / 2
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
    val currentPlacedTowers = gameObserver.placedTowers.toList()
    for (tower in currentPlacedTowers) {
      sprites.draw(
        Texture(Textures.tower(tower.type)),
        tower.position.x * gameObserver.gameStage!!.tileWidth,
        tower.position.y * gameObserver.gameStage!!.tileHeight,
        gameObserver.gameStage!!.tileWidth,
        gameObserver.gameStage!!.tileHeight
      )
    }

    sprites.end()
  }

  override fun dispose() {
    super.dispose()

    font.dispose()
    stageMapTexture.dispose()
    stageMap.clearListeners()
    selectedTexture.dispose()
    notSelectedTexture.dispose()

    for (towerTexture in towerShopTextures) {
      towerTexture.dispose()
    }

    println("PlayStatePlacement disposed")
  }
}