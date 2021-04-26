package com.coronadefense.receiver.messages

data class GameModeMessage(
    val stageNumber: Int,
    val difficulty: Int
): IMessage
