package com.coronadefense.receiver.messages

data class PingMessage(
    val majorVersion: UByte, 
    val minorVersion: UByte,
): IMessage
