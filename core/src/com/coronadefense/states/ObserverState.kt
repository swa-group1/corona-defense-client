package com.coronadefense.states

import com.coronadefense.GameStateManager
import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*

abstract class ObserverState(
  stateManager: GameStateManager
): State(stateManager), IReceiverObserver {
  override fun handlePingMessage(message: PingMessage) {
    println("pingMessage received")
    println(message)
  }

  override fun handleFightRoundMessage(message: FightRoundMessage) {
    println("fightRoundMessage received")
    println(message)
  }

  override fun handleGameModeMessage(message: GameModeMessage) {
    println("gameModeMessage received")
    println(message)
  }

  override fun handleInputRoundMessage(message: InputRoundMessage) {
    println("inputRoundMessage received")
    println(message)
  }

  override fun handleLobbyModeMessage(message: LobbyModeMessage) {
    println("lobbyModeMessage received")
    println(message)
  }

  override fun handleEndGameMessage(message: EndGameMessage) {
    println("endGameMessage received")
    println(message)
  }

  override fun handleHealthUpdateMessage(message: HealthUpdateMessage) {
    println("healthUpdateMessage received")
    println(message)
  }

  override fun handleMoneyUpdateMessage(message: MoneyUpdateMessage) {
    println("moneyUpdateMessage received")
    println(message)
  }

  override fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage) {
    println("playerCountUpdateMessage received")
    println(message)
  }

  override fun handleTowerPositionMessage(message: TowerPositionMessage) {
    println("towerPositionMessage received")
    println(message)
  }

  override fun handleTowerRemovedMessage(message: TowerRemovedMessage) {
    println("towerRemovedMessage received")
    println(message)
  }

  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage) {
    println("animationConfirmationMessage received")
    println(message)
  }

  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage) {
    println("boardToPathAnimationMessage received")
    println(message)
  }

  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage) {
    println("pathToPathAnimationMessage received")
    println(message)
  }

  override fun handleTowerAnimationMessage(message: TowerAnimationMessage) {
    println("towerAnimationMessage received")
    println(message)
  }

  override fun handleHealthAnimationMessage(message: HealthAnimationMessage) {
    println("healthAnimationMessage received")
    println(message)
  }

  override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage) {
    println("moneyAnimationMessage received")
    println(message)
  }
}