package com.coronadefense.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3

open class GameObject(
  texturePath: String,
  val x: Float,
  val y: Float
) {
  private val texture: Texture = Texture(texturePath)
  fun draw(sprites: SpriteBatch) {
    sprites.draw(texture, x, y)
  }
  fun dispose() {
    texture.dispose()
  }
}