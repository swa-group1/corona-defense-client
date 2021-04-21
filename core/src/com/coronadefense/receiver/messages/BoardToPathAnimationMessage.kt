package com.coronadefense.receiver.messages

data class BoardToPathAnimationMessage(
    val spriteNumber: Int,
    val startX: Int,
    val startY: Int,
    val endPosition: Float,
    val startTime: Float,
    val endTime: Float,
    val resultAnimation: Int,
): IMessage
