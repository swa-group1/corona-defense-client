package com.coronadefense.views

class MovingGameObject(
  texturePath: String,
  x: Float,
  y: Float,
  val endX: Float,
  val endY: Float,
  val speed: Float
) : GameObject(texturePath, x, y) {

}