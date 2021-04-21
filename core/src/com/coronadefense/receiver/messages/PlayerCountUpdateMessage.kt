package com.coronadefense.receiver.messages

data class PlayerCountUpdateMessage(
    val playerCount: Int,
): IMessage
