package com.coronadefense.receiver

import com.coronadefense.receiver.messages.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

private const val PORT_NUMBER: Int = 19001
private const val SERVER_ADDRESS: String = "35.228.171.73"

@ExperimentalUnsignedTypes
fun main() {
  runBlocking {
    val receiver: Receiver = Receiver(mutableListOf(ReceiverPrinter()))
    println(receiver.connectAsync())
  }
  readLine()
}

/**
 * Object that connects to the backend and listens to broadcaster sending game information.
 * @param observers List of observers to notify about game changes.
 */
class Receiver(private val observers: MutableList<IReceiverObserver>) {
  private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
  private var socket: Socket? = null

  fun addObserver(observer: IReceiverObserver) {
    observers += observer
  }

  fun removeObserver(observer: IReceiverObserver) {
    observers -= observer
  }

  /**
   * Attempt to connect to backend broadcaster.
   * @return The connection number of the
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
    while (true) {
      val byteCode: Byte = input.readByte()
      val lengthByte: Byte = input.readByte()
      val packet: ByteReadPacket = input.readPacket(lengthByte.toUByte().toInt())
      val message: IMessage = getMessageType(byteCode).parse(packet.readBytes())
      notifyObservers(message)
    }
  }

  private fun notifyObservers(message: IMessage) {
    for (observer: IReceiverObserver in this.observers) {
      when (message) {
          is PingMessage -> observer.handlePingMessage(message = message)
          is FightRoundMessage -> observer.handleFightRoundMessage(message = message)
          is GameModeMessage -> observer.handleGameModeMessage(message = message)
          is InputRoundMessage -> observer.handleInputRoundMessage(message = message)
          is LobbyModeMessage -> observer.handleLobbyModeMessage(message = message)
          is EndGameMessage -> observer.handleEndGameMessage(message = message)
          is HealthUpdateMessage -> observer.handleHealthUpdateMessage(message = message)
          is MoneyUpdateMessage -> observer.handleMoneyUpdateMessage(message = message)
          is PlayerCountUpdateMessage -> observer.handlePlayerCountUpdateMessage(message = message)
          is TowerPositionMessage -> observer.handleTowerPositionMessage(message = message)
          is TowerRemovedMessage -> observer.handleTowerRemovedMessage(message = message)
          is AnimationConfirmationMessage -> observer.handleAnimationConfirmationMessage(message = message)
          is BoardToPathAnimationMessage -> observer.handleBoardToPathAnimationMessage(message = message)
          is PathToPathAnimationMessage -> observer.handlePathToPathAnimationMessage(message = message)
          is TowerAnimationMessage -> observer.handleTowerAnimationMessage(message = message)
          is HealthAnimationMessage -> observer.handleHealthAnimationMessage(message = message)
          is MoneyAnimationMessage -> observer.handleMoneyAnimationMessage(message = message)
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
