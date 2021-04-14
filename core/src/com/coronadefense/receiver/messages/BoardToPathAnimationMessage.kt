package com.coronadefense.receiver.messages

class BoardToPathAnimationMessage(
    val spriteNumber: UByte,
    val startX: Ubyte,
    val startY: Ubyte,
    val endPosition: UShort,
    val startTime: Ushort,
    val endTime: Ushort,
    val resultAnimation: Ubyte
): IMessage
