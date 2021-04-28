package com.coronadefense.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.*

/**
 * Class for managing which game state is currently rendered.
 */
class StateManager {
  // Allows for stacking states, though this is currently not really used as we only keep one state in it at a time.
  private val states: Stack<State> = Stack()

  fun push(state: State) {
    states.push(state)
  }

  // When a new game state is set, dispose the old one.
  fun set(state: State) {
    states.pop().dispose()
    states.push(state)
  }

  fun update(deltaTime: Float) {
    states.peek().update(deltaTime)
  }

  fun render(sprites: SpriteBatch) {
    states.peek().render(sprites)
  }
}