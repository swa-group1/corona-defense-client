package com.coronadefense.receiver.messages

class TowerAnimationMessage(
    val towerId: UShort,
    val animationNumber: UByte,
    val rotation: Byte,
): IMessage
