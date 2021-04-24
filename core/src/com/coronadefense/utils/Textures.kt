package com.coronadefense.utils

object Textures {
  fun button(buttonType: String): String {
    return "buttons/$buttonType.png"
  }

  fun stage(stageNumber: Int): String {
    return "stages/stage_${stageNumber.toString().padStart(3, '0')}_img.png"
  }

  fun intruder(spriteNumber: Int): String {
    return "intruders/${spriteNumber.toString().padStart(3, '0')}.png"
  }

  fun tower(typeNumber: Int): String {
    return "towers/1${(typeNumber * 2).toString().padStart(2, '0')}.png"
  }

  fun projectile(spriteNumber: Int): String {
    return "projectiles/${(spriteNumber).toString().padStart(3, '0')}.png"
  }

  fun background(backgroundType: String): String {
    return "backgrounds/${backgroundType}.png"
  }
}