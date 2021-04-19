package com.coronadefense.receiver.messages

data class EndGameMessage(
    val victory: Boolean,
    val onHighScoreList: Int,
    val score: Int,
): IMessage