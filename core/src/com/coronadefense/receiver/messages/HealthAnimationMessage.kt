package com.coronadefense.receiver.messages

data class HealthAnimationMessage(
    val newValue: UShort,
    val time: Float,
): IMessage
