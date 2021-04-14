package com.coronadefense.receiver

import com.coronadefense.receiver.messages.IMessage
import io.ktor.utils.io.*

interface IMessageType {
  val byteCode: Byte
  /**
   *
   */
  fun parse(bytes: ByteArray): IMessage
}