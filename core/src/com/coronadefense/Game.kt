package com.coronadefense

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.receiver.Receiver
import com.coronadefense.states.MenuState

class Game : ApplicationAdapter() {
  companion object {
    const val WIDTH = 800F
    const val HEIGHT = 450f
    const val TITLE = "Corona Defence"
    var batch: SpriteBatch? = null
    val receiver = Receiver(mutableListOf())
  }
  private var stateManager: GameStateManager? = null
  override fun create() {
    batch = SpriteBatch()
    stateManager = GameStateManager()
    Gdx.input.inputProcessor = InputMultiplexer()
    Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
    stateManager?.push(MenuState(stateManager!!))
  }
  override fun render() {
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
    stateManager?.update(Gdx.graphics.deltaTime)
    stateManager?.render(batch!!)
  }
  override fun dispose() {
    batch?.dispose()
  }
}