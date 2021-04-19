package com.coronadefense.receiver.messages

data class EndGameMessage(
    val victory: Byte,
    val onHighScoreList: Byte,
    val score: UInt,
): IMessage