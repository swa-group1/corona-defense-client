package com.coronadefense.receiver.messages

class TowerPositionMessage(
    val towerId: UShort,
    val typeNumber: UByte,
    val xPosition: UByte,
    val yPosition: UByte,
): IMessage
