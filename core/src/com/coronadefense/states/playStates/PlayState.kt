package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
import com.coronadefense.states.StateManager
import com.coronadefense.states.menuStates.MainMenuState
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LEAVE_GAME_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures

abstract class PlayState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  protected val font = Font(20)

  protected val centerPositionX: Float = GAME_WIDTH - SIDEBAR_WIDTH * 0.5f

  private val leaveButtonText = "LEAVE GAME"

  init {
    val leaveButton = Image()
    buttons += leaveButton

    leaveButton.setSize(SIDEBAR_WIDTH, LEAVE_GAME_BUTTON_HEIGHT)
    leaveButton.setPosition(centerPositionX - SIDEBAR_WIDTH * 0.5f, 0f)

    leaveButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        gameObserver.gameState = "leave"
      }
    })

    stage.addActor(leaveButton)
  }

  fun update(): Boolean {
    if (gameObserver.gameState == "leave") {
      gameObserver.leaveLobby()
      stateManager.set(MainMenuState(stateManager))
      return true
    }
    return false
  }

  fun renderSidebar(sprites: SpriteBatch) {
    sprites.draw(
      Textures.background("sidebar"),
      GAME_WIDTH - SIDEBAR_WIDTH,
      0f,
      SIDEBAR_WIDTH,
      GAME_HEIGHT
    )
    sprites.draw(
      Textures.button("gray"),
      centerPositionX - SIDEBAR_WIDTH * 0.5f,
      0f,
      SIDEBAR_WIDTH,
      LEAVE_GAME_BUTTON_HEIGHT
    )
    font.draw(
      sprites,
      leaveButtonText,
      centerPositionX - font.width(leaveButtonText) * 0.5f,
      (LEAVE_GAME_BUTTON_HEIGHT + font.height(leaveButtonText)) * 0.5f
    )
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    font.dispose()
  }
}