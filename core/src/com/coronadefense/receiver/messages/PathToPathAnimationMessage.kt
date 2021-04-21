package com.coronadefense.receiver.messages

data class PathToPathAnimationMessage(
    val spriteNumber: Int,
    val startPosition: Float,
    val endPosition: Float,
    val startTime: Float,
    val endTime: Float,
    val resultAnimation: Int,
): IMessage
