package com.coronadefense.states.playStates

object Textures {
  val towers: Map<UByte, String> = hashMapOf(
    100.toUByte() to "towers/100.png",
    102.toUByte() to "towers/102.png",
    104.toUByte() to "towers/104.png",
          106.toUByte() to "towers/106.png"
  )
  val stages: Map<Int, String> = hashMapOf(
    0 to "stages/samfundetStage.png"
  )
  val intruders: Map<UByte, String> = hashMapOf(
          0.toUByte() to "intruders/000.png",
          5.toUByte() to "intruders/005.png",
          6.toUByte() to "intruders/006.png",
          10.toUByte() to "intruders/010.png",
          11.toUByte() to "intruders/011.png",
          12.toUByte() to "intruders/012.png",
          13.toUByte() to "intruders/013.png",
          14.toUByte() to "intruders/014.png",
          15.toUByte() to "intruders/015.png",
          16.toUByte() to "intruders/016.png",
          17.toUByte() to "intruders/017.png",
  )
  val projectiles: Map<UByte, String> = hashMapOf(
          101.toUByte() to "projeciles/101.png",
          103.toUByte() to "projeciles/103.png",
          105.toUByte() to "projeciles/105.png",
          107.toUByte() to "projeciles/107.png"
  )
}