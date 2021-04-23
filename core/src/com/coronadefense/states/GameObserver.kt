package com.coronadefense.states

import com.coronadefense.Game
import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*
import com.coronadefense.types.GameStage
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.types.utils.Position
import kotlinx.coroutines.runBlocking

class GameObserver(
  val lobbyId: Long,
  val lobbyName: String,
  val accessToken: Long,
  var playerCount: Int
): IReceiverObserver {
  var stageNumber: Int? = null
  var gameStage: GameStage? = null
  private val placedTowers: MutableList<Tower> = mutableListOf()

  fun leaveLobby() {
    runBlocking {
      ApiClient.leaveLobbyRequest(lobbyId, accessToken)
    }
    Game.receiver.removeObserver(this)
  }

  override fun handlePingMessage(message: PingMessage) {
    println(message)
  }

  override fun handleFightRoundMessage(message: FightRoundMessage) {
    println(message)
  }

  override fun handleGameModeMessage(message: GameModeMessage) {
    println(message)

    stageNumber = message.stageNumber
  }

  override fun handleInputRoundMessage(message: InputRoundMessage) {
    println(message)
  }

  override fun handleLobbyModeMessage(message: LobbyModeMessage) {
    println(message)
  }

  override fun handleEndGameMessage(message: EndGameMessage) {
    println(message)
  }

  override fun handleHealthUpdateMessage(message: HealthUpdateMessage) {
    println(message)
  }

  override fun handleMoneyUpdateMessage(message: MoneyUpdateMessage) {
    println(message)
  }

  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {
    println(message)

    playerCount = message.playerCount
  }

  override fun handleTowerPositionMessage(message: TowerPositionMessage) {
    println(message)

    placedTowers += Tower(
      message.towerId,
      message.typeNumber,
      Position(message.xPosition, message.yPosition)
    )
  }

  override fun handleTowerRemovedMessage(message: TowerRemovedMessage) {
    println(message)
  }

  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage) {
    println(message)
  }

  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage) {
    println(message)
  }

  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage) {
    println(message)
  }

  override fun handleTowerAnimationMessage(message: TowerAnimationMessage) {
    println(message)
  }

  override fun handleHealthAnimationMessage(message: HealthAnimationMessage) {
    println(message)
  }

  override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage) {
    println(message)
  }
}