package com.coronadefense.receiver.messages

data class PathToPathAnimationMessage(
    val spriteNumber: UByte,
    val startPosition: UShort,
    val endPosition: UShort,
    val startTime: UShort,
    val endTime: UShort,
    val resultAnimation: UByte
): IMessage
