package com.coronadefense.receiver.messages

data class HealthUpdateMessage(
    val newValue: Int,
): IMessage
