package com.coronadefense.receiver

import com.coronadefense.receiver.messages.*
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.concurrent.thread

private const val PORT_NUMBER: Int = 19001
private const val SERVER_ADDRESS: String = "::1"

@ExperimentalUnsignedTypes
fun main() {
    runBlocking {
        val receiver: Receiver = Receiver(listOf())
        println(receiver.connectAsync())
    }
}

class Receiver(private val observers: List<IReceiverObserver>) {
    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
    private var socket: Socket? = null
    private var input: ByteReadChannel? = null

    @ExperimentalUnsignedTypes
    suspend fun connectAsync(): Long {
        socket = socketBuilder.connect(InetSocketAddress(SERVER_ADDRESS, PORT_NUMBER))
        this.input = socket!!.openReadChannel()
        val connectionNumber: Long = input!!.readLong()

        thread {
            runBlocking {
                listen()
            }
        }

        return connectionNumber
    }

    @ExperimentalUnsignedTypes
    private suspend fun listen() {
        while (true){
            input!!.read(min = 2, consumer = ::consumeBytesForward);
        }
    }

    @ExperimentalUnsignedTypes
    private fun consumeBytesForward(buffer: ByteBuffer) {
        runBlocking {
            consumeBytes(buffer)
        }
    }

    @ExperimentalUnsignedTypes
    private suspend fun consumeBytes(buffer: ByteBuffer) {
        val byteCode = buffer[0]
        val length = buffer[1]
        fun consumeMoreBytes(buffer: ByteBuffer){
            val message: IMessage = getMessageType(byteCode).parse(buffer.array())
            notifyObservers(message)
        }
        input!!.read(min = length.toInt(), consumer=::consumeMoreBytes)
    }

    private fun notifyObservers (message: IMessage){
        for (observer:IReceiverObserver in this.observers) {
            when(message) {
                is PingMessage -> observer.handlePingMessage(message = message)
                is FightRoundMessage -> observer.handleFightRoundMessage(message = message)
                is GameModeMessage -> observer.handleGameModeMessage(message = message)
                is InputRoundMessage -> observer.handleInputRoundMessage(message = message)
                is LobbyModeMessage -> observer.handleLobbyModeMessage(message = message)
                is HealthUpdateMessage -> observer.handleHealthUpdateMessage(message = message)
                is MoneyUpdateMessage -> observer.handleMoneyUpdateMessage(message = message)
                is PlayerCountUpdateMessage -> observer.handlePlayerCountUpdateMessage(message = message)
                is TowerPositionMessage -> observer.handleTowerPositionMessage(message = message)
                is TowerRemovedMessage -> observer.handleTowerRemovedMessage(message = message)
                is AnimationConfirmationMessage -> observer.handleAnimationConfirmationMessage(message = message)
                is BoardToPathAnimationMessage -> observer.handleBoardToPathAnimationMessage(message = message)
                is PathToPathAnimationMessage -> observer.handlePathToPathAnimationMessage(message = message)
                is TowerAnimationMessage -> observer.handleTowerAnimationMessage(message = message)
            }
        }
    }

    @ExperimentalUnsignedTypes
    fun getMessageType(byteCode: Byte): MessageType {
        when (byteCode) {
            MessageType.PING.byteCode -> return MessageType.PING
            MessageType.FIGHT_ROUND.byteCode -> return MessageType.FIGHT_ROUND
            MessageType.GAME_MODE.byteCode -> return MessageType.GAME_MODE
            MessageType.INPUT_ROUND.byteCode -> return MessageType.INPUT_ROUND
            MessageType.LOBBY_MODE.byteCode -> return MessageType.LOBBY_MODE
            MessageType.HEALTH_UPDATE.byteCode -> return MessageType.HEALTH_UPDATE
            MessageType.MONEY_UPDATE.byteCode -> return MessageType.MONEY_UPDATE
            MessageType.PLAYER_COUNT_UPDATE.byteCode -> return MessageType.PLAYER_COUNT_UPDATE
            MessageType.TOWER_POSITION.byteCode -> return MessageType.TOWER_POSITION
            MessageType.TOWER_REMOVED.byteCode -> return MessageType.TOWER_REMOVED
            MessageType.ANIMATION_CONFIRMATION.byteCode -> return MessageType.ANIMATION_CONFIRMATION
            MessageType.BOARD_TO_PATH_ANIMATION.byteCode -> return MessageType.BOARD_TO_PATH_ANIMATION
            MessageType.PATH_TO_PATH_ANIMATION.byteCode -> return MessageType.PATH_TO_PATH_ANIMATION
            MessageType.TOWER_ANIMATION.byteCode -> return MessageType.TOWER_ANIMATION
            else -> throw IllegalArgumentException("Byte code $byteCode not valid")
        }
    }
}
