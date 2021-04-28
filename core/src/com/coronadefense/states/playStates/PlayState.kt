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

/**
 * Abstract class with the common state and behavior for all phases of gameplay.
 * Extends InputState to allow user interaction with the Leave Game button, and for subclasses to interact with the game.
 * @param stateManager Manager of all game states.
 * @param gameObserver Observes the Receiver for game updates.
 */
abstract class PlayState(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : InputState(stateManager) {
  protected val font = Font(20)

  protected val centerPositionX: Float = GAME_WIDTH - SIDEBAR_WIDTH * 0.5f

  private val leaveButtonText = "LEAVE GAME"

  init {
    // Adds a Leave Game button in the bottom right corner, for all gameplay phases.
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

  // Returns a boolean for subclasses to check whether the game is being terminated.
  fun update(): Boolean {
    // If the Leave Game button has been clicked, move to main menu.
    if (gameObserver.gameState == "leave") {
      gameObserver.leaveLobby()
      stateManager.set(MainMenuState(stateManager))
      return true
    }
    return false
  }

  // Function for subclasses to place in their own render methods where appropriate, to render the sidebar.
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