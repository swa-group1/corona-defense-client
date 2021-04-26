package com.coronadefense.states

import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.Receiver
import com.coronadefense.receiver.messages.*
import com.coronadefense.types.GameStage
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.utils.DIFFICULTY
import kotlinx.coroutines.runBlocking

class GameObserver(
  val lobbyId: Long,
  val lobbyName: String,
  val accessToken: Long,
  var playerCount: Int
): IReceiverObserver {
  var gameStage: GameStage? = null

  var gameState: String? = null

  val placedTowers: MutableList<Tower> = mutableListOf()
  var money: Int? = null
  var health: Int? = null

  var timeConfirmed: Float = 0f // time confirmed animations
  val pathToPathAnimations = mutableListOf<PathToPathAnimationMessage>() //Intruders
  val healthAnimations = mutableListOf<HealthAnimationMessage>()
  val moneyAnimations = mutableListOf<MoneyAnimationMessage>()
  val boardToPathAnimations = mutableListOf<BoardToPathAnimationMessage>()

  var difficulty: DIFFICULTY? = null

  var endGame = false
  var endGameMessage: EndGameMessage? = null

  var socketClosed = false

  fun leaveLobby() {
    runBlocking {
      try {
        ApiClient.leaveLobbyRequest(lobbyId, accessToken)
      } catch (exception: Exception) {}
    }
    Receiver.observer = null
  }

  override fun handlePingMessage(message: PingMessage) {
    println(message)
  }

  override fun handleFightRoundMessage(message: FightRoundMessage) {
    println(message)
    if (gameState != "leave") {
      gameState = "fight"
    }
  }

  override fun handleGameModeMessage(message: GameModeMessage) {
    println(message)

    difficulty = when (message.difficulty) {
      0 -> DIFFICULTY.EASY
      2 -> DIFFICULTY.HARD
      else -> DIFFICULTY.MEDIUM
    }

    runBlocking {
      gameStage = ApiClient.gameStageRequest(message.stageNumber)
    }
  }

  override fun handleInputRoundMessage(message: InputRoundMessage) {
    println(message)

    if (gameState != "leave") {
      gameState = "input"
    }
  }

  override fun handleLobbyModeMessage(message: LobbyModeMessage) {
    println(message)

    gameState = "lobby"
  }

  override fun handleEndGameMessage(message: EndGameMessage) {
    println(message)

    endGame = true
    endGameMessage=message
  }

  override fun handleHealthUpdateMessage(message: HealthUpdateMessage) {
    println(message)

    health = message.newValue
  }

  override fun handleMoneyUpdateMessage(message: MoneyUpdateMessage) {
    println(message)

    money = message.newValue
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
      message.xPosition,
      message.yPosition
    )
  }

  override fun handleTowerRemovedMessage(message: TowerRemovedMessage) {
    println(message)
  }

  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage) {
    println(message)

    timeConfirmed = message.time
  }

  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage) {
    println(message)

    boardToPathAnimations += message
  }

  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage) {
    println(message)

    pathToPathAnimations += message
  }

  override fun handleTowerAnimationMessage(message: TowerAnimationMessage) {
    println(message)
  }

  override fun handleHealthAnimationMessage(message: HealthAnimationMessage) {
    println(message)

    healthAnimations += message
  }

  override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage) {
    println(message)

    moneyAnimations += message
  }

  override fun handleSocketClosed() {
    println("Socket closed")

    socketClosed = true
  }
}