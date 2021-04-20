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
  private val singlePlayer = Image(Texture("greenBorder.png"))
  private val multiPlayer= Image(Texture("greenBorder.png"))
  private val highScore= Image(Texture("greenBorder.png"))
  private val font = Font.generateFont(20)

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage);
    }
    singlePlayer.setSize(180f, 60f)
    singlePlayer.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-30)
    singlePlayer.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {

        stateManager.set(MenuState(stateManager))
      }
    })
    stage.addActor(singlePlayer)

    multiPlayer.setSize(180f, 60f)
    multiPlayer.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-120)
    multiPlayer.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        stateManager.set(LobbyListState(stateManager))
      }
    })
    stage.addActor(multiPlayer)

    highScore.setSize(180f, 60f)
    highScore.setPosition(Game.WIDTH/2-90, Game.HEIGHT/2-210)
    highScore.addListener(object : ClickListener() {
      override fun clicked(event: InputEvent?, x: Float, y: Float) {
        stateManager.set(HighscoreState(stateManager))
      }
    })
    stage.addActor(highScore)
  }


  override fun handleInput() {

  }

  override fun update(deltaTime: Float) {
    handleInput()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    font.draw(sprites, "SINGLEPLAYER", Game.WIDTH/2-70, Game.HEIGHT/2+5)
    font.draw(sprites, "MULTIPLAYER", Game.WIDTH/2-65, Game.HEIGHT/2-85)
    font.draw(sprites, "HIGHSCORES", Game.WIDTH/2-60, Game.HEIGHT/2-175)
    sprites.end()
    stage.draw()
  }

  override fun dispose() {
    background.dispose()
    stage.dispose()
    println("Menu State Disposed")
  }
}