package com.coronadefense.receiver.messages

data class TowerRemovedMessage(
    val towerId: Int,
): IMessage
