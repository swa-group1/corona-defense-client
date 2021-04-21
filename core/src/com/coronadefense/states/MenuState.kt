package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
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
import com.coronadefense.utils.Font

class MenuState(stateManager: GameStateManager): State(stateManager) {
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
  }

  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)

  private val stage: Stage = Stage(viewport, Game.batch)

  private val background = Texture("initiate_game_state.jpg")
  private val font = Font.generateFont(20)

  private var nextState: State? = null

  val singlePlayerTexture = Texture("greenBorder.png")
  val singlePlayerButton = Image(singlePlayerTexture)

  val multiPlayerTexture = Texture("greenBorder.png")
  val multiPlayerButton = Image(multiPlayerTexture)

  val highScoresTexture = Texture("greenBorder.png")
  val highScoresButton = Image(highScoresTexture)

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }

    singlePlayerButton.setSize(180f, 60f)
    singlePlayerButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-30)
    singlePlayerButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        stateManager.set(MenuState(stateManager))
      }
    })
    stage.addActor(singlePlayerButton)

    multiPlayerButton.setSize(180f, 60f)
    multiPlayerButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-120)
    multiPlayerButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        nextState = LobbyListState(stateManager)
      }
    })
    stage.addActor(multiPlayerButton)

    highScoresButton.setSize(180f, 60f)
    highScoresButton.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-210)
    highScoresButton.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        nextState = HighscoreListState(stateManager)
      }
    })
    stage.addActor(highScoresButton)
  }


  override fun handleInput() {}

  override fun update(deltaTime: Float) {}

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    font.draw(sprites, "SINGLEPLAYER", Game.WIDTH/2-70, Game.HEIGHT/2+5)
    font.draw(sprites, "MULTIPLAYER", Game.WIDTH/2-65, Game.HEIGHT/2-85)
    font.draw(sprites, "HIGHSCORES", Game.WIDTH/2-60, Game.HEIGHT/2-175)
    sprites.end()
    stage.draw()
    nextState?.let {
      stateManager.set(nextState!!)
    }
  }

  override fun dispose() {
    background.dispose()
    font.dispose()
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.removeProcessor(stage)
    }
    stage.clear()
    stage.dispose()

    singlePlayerTexture.dispose()
    singlePlayerButton.clearListeners()

    multiPlayerTexture.dispose()
    multiPlayerButton.clearListeners()

    highScoresTexture.dispose()
    highScoresButton.clearListeners()

    println("Menu State Disposed")
  }
}