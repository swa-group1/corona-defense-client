package com.coronadefense.receiver

import com.coronadefense.receiver.messages.*

/**
 * Interface that should be implemented to be able to listen to receiver.
 */
interface IReceiverObserver {
  fun handlePingMessage(message: PingMessage)
  fun handleFightRoundMessage(message: FightRoundMessage)
  fun handleGameModeMessage(message: GameModeMessage)
  fun handleInputRoundMessage(message: InputRoundMessage)
  fun handleLobbyModeMessage(message: LobbyModeMessage)
  fun handleEndGameMessage(message: EndGameMessage)
  fun handleHealthUpdateMessage(message: HealthUpdateMessage)
  fun handleMoneyUpdateMessage(message: MoneyUpdateMessage)
  fun handlePlayerCountUpdateMessage(message: PlayerCountUpdateMessage)
  fun handleTowerPositionMessage(message: TowerPositionMessage)
  fun handleTowerRemovedMessage(message: TowerRemovedMessage)
  fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage)
  fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage)
  fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage)
  fun handleTowerAnimationMessage(message: TowerAnimationMessage)
  fun handleHealthAnimationMessage(message: HealthAnimationMessage)
  fun handleMoneyAnimationMessage(message: MoneyAnimationMessage)
}