package com.coronadefense.states

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import java.util.*

class StateManager {

    private val states: Stack<State> = Stack()
    fun push(state: State) {
        states.push(state)
    }
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