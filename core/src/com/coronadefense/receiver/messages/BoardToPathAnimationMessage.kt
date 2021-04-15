package com.coronadefense.receiver.messages

class BoardToPathAnimationMessage(
    val spriteNumber: UByte,
    val startX: UByte,
    val startY: UByte,
    val endPosition: UShort,
    val startTime: UShort,
    val endTime: UShort,
    val resultAnimation: UByte
): IMessage
