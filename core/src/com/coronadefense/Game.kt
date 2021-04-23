package com.coronadefense

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.receiver.Receiver
import com.coronadefense.states.GameStateManager
import com.coronadefense.states.menuStates.MenuState

class Game : ApplicationAdapter() {
  companion object {
    val receiver = Receiver(mutableListOf())
    var sprites: SpriteBatch? = null
  }
  private var stateManager: GameStateManager? = null
  override fun create() {
    sprites = SpriteBatch()
    stateManager = GameStateManager()
    Gdx.input.inputProcessor = InputMultiplexer()
    Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
    stateManager?.push(MenuState(stateManager!!))
  }
  override fun render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stateManager?.update(Gdx.graphics.deltaTime)
    stateManager?.render(sprites!!)
  }
  override fun dispose() {
    sprites?.dispose()
  }
}