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
import com.coronadefense.states.State
import com.coronadefense.states.playStates.Textures
import com.coronadefense.types.Lobby
import com.coronadefense.utils.Font
import kotlinx.coroutines.runBlocking

class PlayStatePlacement(
  stateManager: GameStateManager,
  val lobby: Lobby,
  stageNumber: Int
) : State(stateManager), IReceiverObserver {
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background: Texture = Texture(Textures.stages[stageNumber])
  private val font: BitmapFont = Font.generateFont(20)

  private var gameStage: GameStage? = null
  init {
    println("PlayState!!!")
    runBlocking {
      gameStage = ApiClient.gameStageRequest(stageNumber)
    }
    println(gameStage)
  }

  private var towerTypeToPlace: Int? = null

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    val towerShopX: Float = Game.WIDTH / 2 - 160
    for ((index, key) in Textures.towers.keys.withIndex()) {
      val towerShopY: Float = (Game.HEIGHT / 2) + 17f - (30f * index)
      val towerButton = Image(Texture(Textures.towers[key]))
      towerButton.setSize(310f, 30f)
      towerButton.setPosition(towerShopX, towerShopY)
      towerButton.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          towerTypeToPlace = key.toInt()
        }
      })
      stage.addActor(towerButton)
    }
  }

  override fun handleInput() {}

  override fun update(deltaTime: Float) {}

  override fun render(sprites: SpriteBatch) {}

  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    font.dispose()
    background.dispose()
  }

  override fun handlePingMessage(message: PingMessage){}
  override fun handleFightRoundMessage(message: FightRoundMessage){}
  override fun handleGameModeMessage(message: GameModeMessage) {}
  override fun handleInputRoundMessage(message: InputRoundMessage){}
  override fun handleEndGameMessage(message: EndGameMessage){}
  override fun handleLobbyModeMessage(message: LobbyModeMessage){}
  override fun handleHealthAnimationMessage(message: HealthAnimationMessage){}
  override fun handleHealthUpdateMessage(message: HealthUpdateMessage){}
  override fun handleMoneyUpdateMessage(message: MoneyUpdateMessage){}
  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {}
  override fun handleTowerPositionMessage(message: TowerPositionMessage){}
  override fun handleTowerRemovedMessage(message: TowerRemovedMessage){}
  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage){}
  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage){}
  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage){}
  override fun handleTowerAnimationMessage(message: TowerAnimationMessage){}
  override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage){}
}