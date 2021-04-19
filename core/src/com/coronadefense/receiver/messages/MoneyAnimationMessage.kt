package com.coronadefense.receiver.messages

data class MoneyAnimationMessage(
    val newValue: Int,
    val time: Float,
): IMessage
