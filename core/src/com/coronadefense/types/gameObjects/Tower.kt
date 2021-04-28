package com.coronadefense.types.gameObjects

/**
 * Class to store the state of a placed tower.
 * @param id The ID of the placed tower.
 * @param type The type of the placed tower.
 * @param positionX The X position (cell-wise) of the placed tower.
 * @param positionY The Y position (cell-wise) of the placed tower.
 */
data class Tower (
  val id: Int,
  val type: Int,
  val positionX: Int,
  val positionY: Int
)