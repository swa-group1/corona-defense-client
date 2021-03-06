package com.coronadefense.receiver

import com.coronadefense.receiver.messages.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetSocketAddress

private const val PORT_NUMBER: Int = 19001
private const val SERVER_ADDRESS: String = "35.228.171.73"

/**
 * Object that connects to the backend and listens to broadcaster sending game information.
 */
object Receiver {
  // Single observer, since one client will only ever have one observer to the Receiver.
  // This makes it easier to remove the observer when the client no longer needs to listen.
  var observer: IReceiverObserver? = null

  private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
  private var socket: Socket? = null

  /**
   * Attempt to connect to backend broadcaster.
   * @return The connection number of the socket
   */
  @ExperimentalUnsignedTypes
  suspend fun connectAsync(): Long {
    socket = socketBuilder.connect(InetSocketAddress(SERVER_ADDRESS, PORT_NUMBER))
    val input: ByteReadChannel = socket!!.openReadChannel()
    val connectionNumber: Long = input.readLong()

    GlobalScope.launch(Dispatchers.IO, block = {
      listen(input)
    })

    return connectionNumber
  }

  @ExperimentalUnsignedTypes
  private suspend fun listen(input: ByteReadChannel) {
    try {
      while (true) {
        val byteCode: Byte = input.readByte()
        val lengthByte: Byte = input.readByte()
        val packet: ByteReadPacket = input.readPacket(lengthByte.toUByte().toInt())
        val message: IMessage = getMessageType(byteCode).parse(packet.readBytes())
        notifyObservers(message)
        if (byteCode == MessageType.END_GAME.byteCode) {
          break
        }
      }
    } catch (e: Exception) {
      observer?.handleSocketClosed()
    }
  }

  private fun notifyObservers(message: IMessage) {
    observer?.let {
      when (message) {
        is PingMessage -> observer!!.handlePingMessage(message = message)
        is FightRoundMessage -> observer!!.handleFightRoundMessage(message = message)
        is GameModeMessage -> observer!!.handleGameModeMessage(message = message)
        is InputRoundMessage -> observer!!.handleInputRoundMessage(message = message)
        is LobbyModeMessage -> observer!!.handleLobbyModeMessage(message = message)
        is EndGameMessage -> observer!!.handleEndGameMessage(message = message)
        is HealthUpdateMessage -> observer!!.handleHealthUpdateMessage(message = message)
        is MoneyUpdateMessage -> observer!!.handleMoneyUpdateMessage(message = message)
        is PlayerCountUpdateMessage -> observer!!.handlePlayerCountUpdateMessage(message = message)
        is TowerPositionMessage -> observer!!.handleTowerPositionMessage(message = message)
        is TowerRemovedMessage -> observer!!.handleTowerRemovedMessage(message = message)
        is AnimationConfirmationMessage -> observer!!.handleAnimationConfirmationMessage(message = message)
        is BoardToPathAnimationMessage -> observer!!.handleBoardToPathAnimationMessage(message = message)
        is PathToPathAnimationMessage -> observer!!.handlePathToPathAnimationMessage(message = message)
        is TowerAnimationMessage -> observer!!.handleTowerAnimationMessage(message = message)
        is HealthAnimationMessage -> observer!!.handleHealthAnimationMessage(message = message)
        is MoneyAnimationMessage -> observer!!.handleMoneyAnimationMessage(message = message)
      }
    }
  }

  @ExperimentalUnsignedTypes
  private fun getMessageType(byteCode: Byte): MessageType {
    for (messageType: MessageType in MessageType.values()) {
      if (messageType.byteCode == byteCode) {
        return messageType
      }
    }
    throw IllegalArgumentException("Byte code $byteCode not valid")
  }
}
