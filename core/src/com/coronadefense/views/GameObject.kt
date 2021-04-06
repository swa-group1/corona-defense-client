package com.coronadefense.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch

open class GameObject(
  texturePath: String,
  var x: Float,
  var y: Float
) {
  private val texture: Texture = Texture(texturePath)
  fun draw(sprites: SpriteBatch) {
    sprites.draw(texture, x, y)
  }
  fun dispose() {
    texture.dispose()
  }
}