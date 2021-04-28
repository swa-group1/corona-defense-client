package com.coronadefense.receiver

import com.coronadefense.receiver.messages.IMessage

/**
 * Interface for the different kinds of messages received from the broadcaster.
 */
interface IMessageType {
  /**
   * Byte code used to differentiate between message types.
   * Example: Ping has bytecode 0x10.
   */
  val byteCode: Byte

  /**
   * Parse bytes into a message of this type.
   * @param bytes The bytes to parse. Does not validate that the byteArray is of the correct length.
   */
  fun parse(bytes: ByteArray): IMessage
}