package com.coronadefense

import com.coronadefense.receiver.messages.IMessage

class FightRoundMessage: IMessage(
    val roundNumber:Short 
)
