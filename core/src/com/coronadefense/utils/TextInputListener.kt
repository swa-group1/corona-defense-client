package com.coronadefense.utils

import com.badlogic.gdx.Input.TextInputListener

class TextInputListener : TextInputListener {
  var value: String = ""

  override fun input(text: String) {
    value = text
  }

  override fun canceled() {
    value = ""
  }

  fun dispose() { /* Nothing to dispose */ }
}