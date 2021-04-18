package com.coronadefense.receiver.messages

data class MoneyAnimationMessage(
    val newValue: UInt,
    val time: Float,
): IMessage
