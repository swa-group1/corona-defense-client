package com.coronadefense.views

import com.badlogic.gdx.math.Vector3
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class MovingGameObject(
  texturePath: String,
  x: Float,
  y: Float,
  targetPositions: Array<Pair<Float, Float>>,
  val endX: Float,
  val endY: Float,
  val speed: Float
) : GameObject(texturePath, x, y) {
  private val angle = atan((endY-y)/(endX-x))
  private val velocity: Vector3 = Vector3(
    speed * cos(angle),
    speed * sin(angle),
    0f
  )
  fun update(deltaTime: Float) {
    velocity.scl(deltaTime)
    this.x += velocity.x
    this.y += velocity.y
    velocity.scl(1/deltaTime)
  }
}