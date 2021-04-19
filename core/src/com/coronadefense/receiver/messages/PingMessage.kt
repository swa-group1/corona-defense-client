package com.coronadefense.receiver.messages

data class PingMessage(
    val majorVersion: Int,
    val minorVersion: Int,
): IMessage
