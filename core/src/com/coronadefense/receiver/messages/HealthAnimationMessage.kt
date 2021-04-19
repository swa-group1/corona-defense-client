package com.coronadefense.receiver.messages

data class HealthAnimationMessage(
    val newValue: Int,
    val time: Float,
): IMessage
