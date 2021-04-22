package com.coronadefense.states.playStates

object Textures {
  fun stagePath(stageNumber: Int): String {
    return "stages/stage_${stageNumber.toString().padStart(3, '0')}_img.png"
  }
  fun intruderPath(spriteNumber: UByte): String {
    return "intruders/${spriteNumber.toString().padStart(3, '0')}.png"
  }
  fun towerPath(typeNumber: Int): String {
    return "towers/1${((typeNumber - 1) * 2).toString().padStart(2, '0')}.png"
  }
  fun projectilePath(towerTypeNumber: Int): String {
    return "projectiles/1${((towerTypeNumber * 2) - 1).toString().padStart(2, '0')}.png"
  }
}