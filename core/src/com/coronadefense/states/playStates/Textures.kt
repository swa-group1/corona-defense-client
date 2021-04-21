package com.coronadefense.states.playStates

object Textures {
  val towers: Map<UByte, String> = hashMapOf(
    0.toUByte() to "towers/nurse.png",
    1.toUByte() to "towers/hospital.png",
    2.toUByte() to "towers/government.png"
  )
  val stages: Map<UByte, String> = hashMapOf(
    0.toUByte() to "stages/samfundetStage.png"
  )
}