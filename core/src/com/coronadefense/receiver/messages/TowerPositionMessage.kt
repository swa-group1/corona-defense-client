package com.coronadefense.receiver.messages

data class TowerPositionMessage(
    val towerId: Int,
    val typeNumber: Int,
    val xPosition: Int,
    val yPosition: Int,
): IMessage
