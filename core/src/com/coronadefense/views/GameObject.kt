package com.coronadefense.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2

open class GameObject(
  texturePath: String,
  var position: Vector2
) {
  private val texture: Texture = Texture(texturePath)
  fun draw(sprites: SpriteBatch) {
    sprites.draw(texture, position.x, position.y)
  }
  fun dispose() {
    texture.dispose()
  }
}