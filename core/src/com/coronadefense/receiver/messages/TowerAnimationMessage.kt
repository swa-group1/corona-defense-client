package com.coronadefense.receiver.messages

data class TowerAnimationMessage(
    val towerId: UShort,
    val animationNumber: UByte,
    val rotation: Byte,
    val time: Float,
): IMessage