package com.coronadefense.types.gameObjects

import com.coronadefense.types.utils.Position

data class Tower (
  val id: Int,
  val type: Int,
  val position: Position,
)