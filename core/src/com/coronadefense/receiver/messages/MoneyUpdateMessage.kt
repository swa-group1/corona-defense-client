package com.coronadefense.receiver.messages

data class MoneyUpdateMessage(
    val newValue: Int,
): IMessage
