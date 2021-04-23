package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.coronadefense.GameStage

open class GameObject(
  texturePath: String,
  var position: GameStage.Point
) {
  private val texture: Texture = Texture(texturePath)
  fun draw(sprites: SpriteBatch) {
    sprites.draw(texture, position.X.toFloat(), position.Y.toFloat())
  }
  fun dispose() {
    texture.dispose()
  }
}