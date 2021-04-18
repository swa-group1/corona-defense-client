package com.coronadefense.receiver.messages

data class PathToPathAnimationMessage(
    val spriteNumber: UByte,
    val startPosition: Float,
    val endPosition: Float,
    val startTime: Float,
    val endTime: Float,
    val resultAnimation: UByte,
): IMessage
