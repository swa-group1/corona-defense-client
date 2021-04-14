package com.coronadefense.receiver.messages

class PingMessage(
    val majorVersion: UByte, 
    val minorVersion: UByte
): IMessage
