package com.coronadefense.states.menuStates

import com.badlogic.gdx.graphics.Texture
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
import java.util.*

class MainMenuState(
  stateManager: StateManager
): InputState(stateManager) {
  private val background = Texture(Textures.background("menu"))
  private val font = Font(20)

  private val menuActions: MutableMap<String, Boolean> = mutableMapOf(
    "play" to false,
    "highscores" to false,
    "tutorial" to false
  )

  init {
    for ((menuIndex, menuAction) in menuActions.keys.withIndex()) {
      val buttonTexture = Texture(Textures.button("standard"))
      textures += buttonTexture

      val button = Image(buttonTexture)
      buttons += button

      button.setSize(MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT)
      button.setPosition(
        (GAME_WIDTH - MENU_BUTTON_WIDTH) / 2,
        (GAME_HEIGHT) / 2 + MENU_TITLE_OFFSET - (MENU_BUTTON_HEIGHT + MENU_BUTTON_SPACING) * (menuIndex + 1)
      )

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
          "play" -> return stateManager.set(LobbyListState(stateManager))
          "highscores" -> return stateManager.set(HighscoreListState(stateManager))
          "tutorial" -> return stateManager.set(TutorialState(stateManager))
        }
      }
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0F, 0F, GAME_WIDTH, GAME_HEIGHT)

    for ((menuIndex, menuAction) in menuActions.keys.withIndex()) {
      val buttonText = menuAction.toUpperCase(Locale.ROOT)
      font.draw(
        sprites,
        menuAction.toUpperCase(Locale.ROOT),
        (GAME_WIDTH - font.width(buttonText)) / 2,
        (GAME_HEIGHT + MENU_BUTTON_HEIGHT + font.height(buttonText)) / 2 + MENU_TITLE_OFFSET
        - (MENU_BUTTON_HEIGHT + MENU_BUTTON_SPACING) * (menuIndex + 1)
      )
    }

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()

    background.dispose()
    font.dispose()

    println("MenuState disposed")
  }
}