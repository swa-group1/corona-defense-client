package com.coronadefense.receiver

import com.coronadefense.receiver.messages.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

private fun parseFloat(first: Byte, second: Byte, third: Byte, fourth: Byte): Float {
  val bb = ByteBuffer.allocate(4)
  bb.order(ByteOrder.BIG_ENDIAN)
  bb.put(first)
  bb.put(second)
  bb.put(third)
  bb.put(fourth)
  return bb.getFloat(0)
}

@ExperimentalUnsignedTypes
private fun parseUShort(big: Byte, small: Byte): UShort {
  val bb = ByteBuffer.allocate(2)
  bb.order(ByteOrder.BIG_ENDIAN)
  bb.put(big)
  bb.put(small)
  return bb.getShort(0).toUShort()
}

@ExperimentalUnsignedTypes
private fun parseUInt(biggest: Byte, big: Byte, small: Byte, smallest: Byte): UInt {
  val bb = ByteBuffer.allocate(4)
  bb.order(ByteOrder.BIG_ENDIAN)
  bb.put(biggest)
  bb.put(big)
  bb.put(small)
  bb.put(smallest)
  return bb.getInt(0).toUInt()
}

private fun parseBool(byte: Byte): Boolean {
  return byte == 0x01.toByte();
}

/**
 * Enum for parsing of the bytes from all the different sorts of messages from the broadcaster.
 */
@ExperimentalUnsignedTypes
enum class MessageType : IMessageType {
  PING {
    override val byteCode: Byte = 0x10.toByte()

    override fun parse(bytes: ByteArray): PingMessage {
      return PingMessage(
        majorVersion = bytes[0].toUByte().toInt(),
        minorVersion = bytes[1].toUByte().toInt(),
      )
    }
  },
  FIGHT_ROUND {
    override val byteCode: Byte = 0x20.toByte()

    override fun parse(bytes: ByteArray): FightRoundMessage {
      return FightRoundMessage(
        roundNumber = parseUShort(bytes[0], bytes[1]).toInt(),
      )
    }
  },
  GAME_MODE {
    override val byteCode: Byte = 0x21.toByte()

    override fun parse(bytes: ByteArray): GameModeMessage {
      return GameModeMessage(
        stageNumber = bytes[0].toUByte().toInt(),
        difficulty = bytes[1].toUByte().toInt(),
      )
    }
  },
  INPUT_ROUND {
    override val byteCode: Byte = 0x22.toByte()

    override fun parse(bytes: ByteArray): InputRoundMessage {
      return InputRoundMessage(
        roundNumber = parseUShort(bytes[0], bytes[1]).toInt(),
      )
    }
  },
  LOBBY_MODE {
    override val byteCode: Byte = 0x23.toByte()

    override fun parse(bytes: ByteArray): LobbyModeMessage {
      return LobbyModeMessage()
    }
  },
  END_GAME {
    override val byteCode: Byte = 0x24.toByte()

    override fun parse(bytes: ByteArray):EndGameMessage {
      return EndGameMessage(
        victory = parseBool(bytes[0]),
        onHighScoreList = bytes[1].toInt(),
        score = parseUInt(bytes[2], bytes[3], bytes[4], bytes[5],).toInt()
      )
    }
  },
  HEALTH_UPDATE {
    override val byteCode: Byte = 0x30.toByte()

    override fun parse(bytes: ByteArray): HealthUpdateMessage {
      return HealthUpdateMessage(
        newValue = parseUShort(bytes[0], bytes[1]).toInt(),
      )
    }
  },
  MONEY_UPDATE {
    override val byteCode: Byte = 0x31.toByte()

    override fun parse(bytes: ByteArray): MoneyUpdateMessage {
      return MoneyUpdateMessage(
        newValue = parseUInt(bytes[0], bytes[1], bytes[2], bytes[3]).toInt(),
      )
    }
  },
  PLAYER_COUNT_UPDATE {
    override val byteCode: Byte = 0x32.toByte()

    override fun parse(bytes: ByteArray): PlayerCountUpdateMessage {
      return PlayerCountUpdateMessage(
        playerCount = bytes[0].toUByte().toInt(),
      )
    }
  },
  TOWER_POSITION {
    override val byteCode: Byte = 0x33.toByte()

    override fun parse(bytes: ByteArray): TowerPositionMessage {
      return TowerPositionMessage(
        towerId = parseUShort(bytes[0], bytes[1]).toInt(),
        typeNumber = bytes[2].toUByte().toInt(),
        xPosition = bytes[3].toUByte().toInt(),
        yPosition = bytes[4].toUByte().toInt(),
      )
    }
  },
  TOWER_REMOVED {
    override val byteCode: Byte = 0x40.toByte()

    override fun parse(bytes: ByteArray): TowerRemovedMessage {
      return TowerRemovedMessage(
        towerId = parseUShort(bytes[0], bytes[1]).toInt(),
      )
    }
  },
  ANIMATION_CONFIRMATION {
    override val byteCode: Byte = 0x50.toByte()

    override fun parse(bytes: ByteArray): AnimationConfirmationMessage {
      return AnimationConfirmationMessage(
        time = parseFloat(bytes[0], bytes[1], bytes[2], bytes[3]),
      )
    }
  },
  BOARD_TO_PATH_ANIMATION {
    override val byteCode: Byte = 0x51.toByte()

    override fun parse(bytes: ByteArray): BoardToPathAnimationMessage {
      return BoardToPathAnimationMessage(
        spriteNumber = bytes[0].toUByte().toInt(),
        startX = bytes[1].toUByte().toInt(),
        startY = bytes[2].toUByte().toInt(),
        endPosition = parseFloat(bytes[3], bytes[4], bytes[5], bytes[6]),
        startTime = parseFloat(bytes[7], bytes[8], bytes[9], bytes[10]),
        endTime = parseFloat(bytes[11], bytes[12], bytes[13], bytes[14]),
        resultAnimation = bytes[15].toUByte().toInt(),
      )
    }
  },
  PATH_TO_PATH_ANIMATION {
    override val byteCode: Byte = 0x52.toByte()

    override fun parse(bytes: ByteArray): PathToPathAnimationMessage {
      return PathToPathAnimationMessage(
        spriteNumber = bytes[0].toUByte().toInt(),
        startPosition = parseFloat(bytes[1], bytes[2], bytes[3], bytes[4]),
        endPosition = parseFloat(bytes[5], bytes[6], bytes[7], bytes[8]),
        startTime = parseFloat(bytes[9], bytes[10], bytes[11], bytes[12]),
        endTime = parseFloat(bytes[13], bytes[14], bytes[15], bytes[16]),
        resultAnimation = bytes[17].toUByte().toInt(),
      )
    }
  },
  TOWER_ANIMATION {
    override val byteCode: Byte = 0x53.toByte()

    override fun parse(bytes: ByteArray): TowerAnimationMessage {
      return TowerAnimationMessage(
        towerId = parseUShort(bytes[0], bytes[1]).toInt(),
        animationNumber = bytes[2].toUByte().toInt(),
        rotation = bytes[3].toInt(),
        time = parseFloat(bytes[4], bytes[5], bytes[6], bytes[7]),
      )
    }
  },
  HEALTH_ANIMATION {
    override val byteCode: Byte = 0x54.toByte()

    override fun parse(bytes: ByteArray): HealthAnimationMessage {
      return HealthAnimationMessage(
        newValue = parseUShort(bytes[0], bytes[1]).toInt(),
        time = parseFloat(bytes[2], bytes[3], bytes[4], bytes[5]),
      )
    }
  },
  MONEY_ANIMATION {
    override val byteCode: Byte = 0x55.toByte()

    override fun parse(bytes: ByteArray): MoneyAnimationMessage {
      return MoneyAnimationMessage(
        newValue = parseUInt(bytes[0], bytes[1], bytes[2], bytes[3]).toInt(),
        time = parseFloat(bytes[4], bytes[5], bytes[6], bytes[7]),
      )
    }
  },
}