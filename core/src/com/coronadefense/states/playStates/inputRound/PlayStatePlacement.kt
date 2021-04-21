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
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.states.State
import com.coronadefense.states.playStates.Textures
import com.coronadefense.types.Lobby
import com.coronadefense.utils.Font
import kotlinx.coroutines.runBlocking

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
  private val background: Texture = Texture(Textures.stages[stageNumber])
  private val font: BitmapFont = Font.generateFont(20)

  private var gameStage: GameStage? = null
  init {
    runBlocking {
      gameStage = ApiClient.gameStageRequest(stageNumber)
    }
    println(gameStage)
  }

  private var towerTypeToPlace: Int? = null

  private val textures: MutableList<Texture> = mutableListOf()
  private val buttons: MutableList<Image> = mutableListOf()

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    val towerShopX: Float = Game.WIDTH / 2 - 160
    var towerIndex = 0
    for ((towerType, texturePath) in Textures.towers) {
      val towerShopY: Float = (Game.HEIGHT / 2) + 17f - (30f * towerIndex)
      towerIndex++
      val towerTexture = Texture(texturePath)
      textures += towerTexture
      val towerButton = Image(towerTexture)
      buttons += towerButton
      towerButton.setSize(310f, 30f)
      towerButton.setPosition(towerShopX, towerShopY)
      towerButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          towerTypeToPlace = towerType.toInt()
        }
      })
      stage.addActor(towerButton)
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    font.draw(sprites, "SHOP", Game.WIDTH / 2 + 100, Game.HEIGHT / 2 + 100)
    sprites.end()
    stage.draw()
  }

  override fun handleInput() {}

  override fun update(deltaTime: Float) {}

  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    font.dispose()
    background.dispose()

    for (texture in textures) {
      texture.dispose()
    }
    for (button in buttons) {
      button.clearListeners()
    }

    println("PlayStatePlacement disposed")
  }
}