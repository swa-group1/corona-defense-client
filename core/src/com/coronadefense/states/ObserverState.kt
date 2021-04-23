package com.coronadefense.states

import com.coronadefense.receiver.IReceiverObserver
import com.coronadefense.receiver.messages.*

abstract class ObserverState(
  stateManager: GameStateManager
): State(stateManager), IReceiverObserver {
  override fun handlePingMessage(message: PingMessage) {
    println(message)
  }

  override fun handleFightRoundMessage(message: FightRoundMessage) {
    println(message)
  }

  override fun handleGameModeMessage(message: GameModeMessage) {
    println(message)
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
  }

  override fun handleTowerPositionMessage(message: TowerPositionMessage) {
    println(message)
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