package com.coronadefense.receiver.messages

data class BoardToPathAnimationMessage(
    val spriteNumber: UByte,
    val startX: UByte,
    val startY: UByte,
    val endPosition: Float,
    val startTime: Float,
    val endTime: Float,
    val resultAnimation: UByte,
): IMessage
