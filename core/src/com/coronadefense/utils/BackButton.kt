package com.coronadefense.utils

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.states.State

class BackButton(
  stateManager: GameStateManager,
  state: State,
  stage: Stage,
) {
  val texture = Texture("backbutton.png")
  val button = Image(texture)
  init {
    button.setSize(100f, 100f)
    button.setPosition(Game.WIDTH/2-350, Game.HEIGHT/2+130)
    button.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        stateManager.set(state)
      }
    })
    stage.addActor(button)
  }
  fun dispose() {
    texture.dispose()
    button.clearListeners()
  }
}