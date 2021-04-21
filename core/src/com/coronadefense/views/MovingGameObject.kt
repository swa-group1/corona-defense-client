package com.coronadefense.views

import com.badlogic.gdx.math.Vector2
import java.util.Stack
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

class MovingGameObject(
  texturePath: String,
  position: Vector2,
  private val targetPositions: Stack<Vector2>,
  private val speed: Float
) : GameObject(texturePath, position) {
  fun calculateVelocity(): Vector2 {
    val angle = atan(
      (targetPositions.peek().x - position.x) / (targetPositions.peek().y - position.x)
    )
    return Vector2(
      speed * cos(angle),
      speed * sin(angle)
    )
  }
  private var velocity: Vector2 = calculateVelocity()
  fun update(deltaTime: Float) {
    velocity.scl(deltaTime)
    val nextPosition = targetPositions.peek()
    if (
      (velocity.x >= 0 && this.position.x + velocity.x > nextPosition.x)
      || (velocity.x < 0 && this.position.x + velocity.x < nextPosition.x)
      || (velocity.y >= 0 && this.position.y + velocity.y > nextPosition.y)
      || (velocity.y < 0 && this.position.y + velocity.y < nextPosition.y)
    ) {
      targetPositions.pop()
      velocity = calculateVelocity()
      velocity.scl(deltaTime)
    }
    this.position.x += velocity.x
    this.position.y += velocity.y
    velocity.scl(1/deltaTime)
  }
}