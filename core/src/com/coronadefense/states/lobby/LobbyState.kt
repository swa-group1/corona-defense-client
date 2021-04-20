package com.coronadefense.states.lobby

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.Lobby
import com.coronadefense.states.State
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*

class LobbyState(stateManager: GameStateManager, lobby: Lobby): State(stateManager), IReceiverObserver  {
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    println("velkommen!!!")
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background: Texture = Texture("initiate_game_state.jpg")

  override fun handleGameModeMessage(message: GameModeMessage) {

  }

  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {

  }

  override fun handleInput() {}
  override fun update(deltaTime: Float) {}
  override fun render(sprites: SpriteBatch) {}
  override fun dispose() {}

  override fun handlePingMessage(message: PingMessage){}
  override fun handleFightRoundMessage(message: FightRoundMessage){}
  override fun handleInputRoundMessage(message: InputRoundMessage){}
  override fun handleLobbyModeMessage(message: LobbyModeMessage){}
  override fun handleHealthUpdateMessage(message: HealthUpdateMessage){}
  override fun handleMoneyUpdateMessage(message: MoneyUpdateMessage){}
  override fun handleTowerPositionMessage(message: TowerPositionMessage){}
  override fun handleTowerRemovedMessage(message: TowerRemovedMessage){}
  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage){}
  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage){}
  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage){}
  override fun handleTowerAnimationMessage(message: TowerAnimationMessage){}
}