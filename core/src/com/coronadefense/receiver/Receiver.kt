package com.coronadefense.receiver

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

private const val PORT_NUMBER: Int = 19001
private const val SERVER_ADDRESS: String = "::1";

fun main() {
    runBlocking {
        val receiver: Receiver = Receiver()
        receiver.connectAsync()
    }
}

class Receiver(private val observers: List<IReceiverObserver>) {
    private val socketBuilder = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
    private var socket: Socket? = null

    suspend fun connectAsync(): Long {
        socket = socketBuilder.connect(InetSocketAddress(SERVER_ADDRESS, PORT_NUMBER))
        val input: ByteReadChannel = socket!!.openReadChannel()
        val connectionNumber: Long = input.readLong()

        // Begin other stuff

        return connectionNumber
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