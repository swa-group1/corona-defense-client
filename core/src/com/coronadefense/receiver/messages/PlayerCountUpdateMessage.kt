package com.coronadefense.receiver.messages

data class PlayerCountUpdateMessage(
    val playerCount: UByte, 
): IMessage
