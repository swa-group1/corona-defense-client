package com.coronadefense.types.gameObjects

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.Game
import com.coronadefense.GameStage


interface MovingGameObject{
  val texture: Texture
  fun draw(sprite: SpriteBatch)
  fun update(deltaTime: Float)
}