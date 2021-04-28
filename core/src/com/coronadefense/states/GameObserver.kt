package com.coronadefense.states

import com.coronadefense.api.ApiClient
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.Receiver
import com.coronadefense.receiver.messages.*
import com.coronadefense.types.GameStage
import com.coronadefense.types.gameObjects.Tower
import com.coronadefense.utils.DIFFICULTY
import kotlinx.coroutines.runBlocking

/**
 * Class for observing the Receiver, and store the state of the game for use by the Lobby-/PlayStates.
 * @param lobbyId ID of the lobby connected to this game.
 * @param lobbyName Name of the lobby connected to this game.
 * @param accessToken Token for the current connection, for use in API requests.
 * @param playerCount Initial player count.
 */
class GameObserver(
  val lobbyId: Long,
  val lobbyName: String,
  val accessToken: Long,
  var playerCount: Int
): IReceiverObserver {
  // Stage to be fetched after StartGameRequest.
  var gameStage: GameStage? = null

  // State of the current game
  // "fight" for wave phase
  // "input" for placement phase
  // "lobby" for game setup
  // "leave" for terminating the game
  var gameState: String? = null

  val placedTowers: MutableList<Tower> = mutableListOf()
  var money: Int? = null
  var health: Int? = null

  var timeConfirmed: Float = 0f // time confirmed animations

  // Lists of animations to be rendered in turn by PlayStateWave.
  val pathToPathAnimations = mutableListOf<PathToPathAnimationMessage>() // Intruders
  val boardToPathAnimations = mutableListOf<BoardToPathAnimationMessage>() // Projectiles
  val healthAnimations = mutableListOf<HealthAnimationMessage>() // Health point updates
  val moneyAnimations = mutableListOf<MoneyAnimationMessage>()  // Money updates

  // Selected difficulty of the game.
  var difficulty: DIFFICULTY? = null

  var endGame = false
  var endGameMessage: EndGameMessage? = null

  // Whether the lobby has timed out.
  var socketClosed = false

  // Function to leave the lobby and disconnect from the Receiver.
  // Empty catch block, since a failed LeaveLobbyRequest means that the lobby is already terminated backend.
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

  // When the game is started, store the difficulty and fetch the GameStage.
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

  // Sell tower functionality not implemented frontend due to time constraints.
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