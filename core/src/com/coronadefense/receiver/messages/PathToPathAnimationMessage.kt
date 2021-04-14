package com.coronadefense.receiver.messages

class PathToPathAnimationMessage(
    val spriteNumber: UByte,
    val startPosition: UShort,
    val endPosition: UShort,
    val startTime: Ushort,
    val endTime: Ushort,
    val resultAnimation: Ubyte
): IMessage
