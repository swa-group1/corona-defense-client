package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.states.lobby.LobbyListState
import com.coronadefense.utils.Textures
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font

class MenuState(stateManager: GameStateManager): State(stateManager) {
  init {
    camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
  }
  private val viewport: Viewport = StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.sprites)

  private val background = Texture("initiate_game_state.jpg")
  private val font = Font.generateFont(20)

  private val menuActions: MutableMap<String, Boolean> = mutableMapOf(
    "play" to false,
    "highscores" to false
  )

  private val buttonTextures: MutableList<Texture> = mutableListOf()
  private val buttons: MutableList<Image> = mutableListOf()

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
    for ((menuIndex, menuAction) in menuActions.keys.withIndex()) {
      val buttonTexture = Texture(Textures.buttonPath("standard"))
      buttonTextures += buttonTexture
      val button = Image(buttonTexture)
      button.setSize(Constants.MENU_BUTTON_WIDTH, Constants.MENU_BUTTON_HEIGHT)
      button.setPosition(
        (Constants.GAME_WIDTH - Constants.MENU_BUTTON_WIDTH) / 2,
        (Constants.GAME_HEIGHT - Constants.MENU_BUTTON_HEIGHT) / 2
        - (Constants.MENU_BUTTON_HEIGHT * 3/2) * menuIndex
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
        }
      }
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    for ((menuIndex, menuAction) in menuActions.keys.withIndex()) {
      val buttonText = menuAction.toUpperCase()
      font.draw(
        sprites,
        menuAction.toUpperCase(),
        (Constants.GAME_WIDTH - Font.textWidth(font, buttonText)) / 2,
        Constants.GAME_HEIGHT / 2 + 5f - (Constants.MENU_BUTTON_HEIGHT * 3/2) * menuIndex
      )
    }
    sprites.end()
    stage.draw()
  }

  override fun dispose() {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()
    background.dispose()
    font.dispose()
    for (texture in buttonTextures) {
      texture.dispose()
    }
    for (button in buttons) {
      button.clearListeners()
    }
    println("MenuState disposed")
  }
}