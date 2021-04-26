package com.coronadefense.states.menuStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.coronadefense.states.StateManager
import com.coronadefense.states.InputState
import com.coronadefense.utils.Textures
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.MENU_BUTTON_HEIGHT
import com.coronadefense.utils.Constants.MENU_BUTTON_SPACING
import com.coronadefense.utils.Constants.MENU_BUTTON_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import com.coronadefense.utils.Font

class MainMenuState(
  stateManager: StateManager
): InputState(stateManager) {
  private val font = Font(20)

  private val centerPositionX: Float = GAME_WIDTH * 0.5f
  private val buttonPositionsY: MutableList<Float> = mutableListOf()

  private val menuActions: MutableMap<String, Boolean> = mutableMapOf(
    "PLAY" to false,
    "HIGHSCORES" to false,
    "TUTORIAL" to false
  )

  init {
    for ((index, menuAction) in menuActions.keys.withIndex()) {
      val button = Image()
      buttons += button

      val buttonPositionY = GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - (MENU_BUTTON_HEIGHT + MENU_BUTTON_SPACING) * (index + 1)
      buttonPositionsY += buttonPositionY

      button.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
      button.setPosition(centerPositionX - MENU_BUTTON_WIDTH * 0.5f, buttonPositionY)

      button.addListener(object : ClickListener() {
        override fun clicked(event: InputEvent?, x: Float, y: Float) {
          menuActions[menuAction] = true
        }
      })

      stage.addActor(button)
    }
  }

  override fun update(deltaTime: Float) {
    for ((menuAction, execute) in menuActions) {
      if (execute) {
        when (menuAction) {
          "PLAY" -> return stateManager.set(LobbyListState(stateManager))
          "HIGHSCORES" -> return stateManager.set(HighscoreListState(stateManager))
          "TUTORIAL" -> return stateManager.set(TutorialState(stateManager))
        }
      }
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(Textures.background("menu"), 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    for ((index, menuAction) in menuActions.keys.withIndex()) {
      sprites.draw(
        Textures.button("standard"),
        centerPositionX - MENU_BUTTON_WIDTH * 0.5f,
        buttonPositionsY[index],
        MENU_BUTTON_WIDTH,
        MENU_BUTTON_HEIGHT
      )
      font.draw(
        sprites,
        menuAction,
        centerPositionX - font.width(menuAction) * 0.5f,
        buttonPositionsY[index] + (MENU_BUTTON_HEIGHT + font.height(menuAction)) * 0.5f
      )
    }

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    font.dispose()

    println("MenuState disposed")
  }
}