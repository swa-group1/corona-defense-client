package com.coronadefense.utils

object Textures {
  fun buttonPath(buttonType: String): String {
    return "buttons/$buttonType.png"
  }

  fun stagePath(stageNumber: Int): String {
    return "stages/stage_${stageNumber.toString().padStart(3, '0')}_img.png"
  }

  fun intruderPath(spriteNumber: Int): String {
    return "intruders/${spriteNumber.toString().padStart(3, '0')}.png"
  }

  fun towerPath(typeNumber: Int): String {
    return "towers/1${((typeNumber - 1) * 2).toString().padStart(2, '0')}.png"
  }

  fun projectilePath(spriteNumber: Int): String {
    return "projectiles/${(spriteNumber).toString().padStart(3, '0')}.png"
  }
}