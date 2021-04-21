package com.coronadefense.receiver.messages

data class TowerAnimationMessage(
    val towerId: Int,
    val animationNumber: Int,
    val rotation: Int,
    val time: Float,
): IMessage