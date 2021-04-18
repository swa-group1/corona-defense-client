package com.coronadefense.receiver.messages

data class TowerPositionMessage(
    val towerId: UShort,
    val typeNumber: UByte,
    val xPosition: UByte,
    val yPosition: UByte,
): IMessage
