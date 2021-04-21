package com.coronadefense.states

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.HighScore
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Font
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HighscoreListState (stateManager: GameStateManager): State(stateManager){
  var lobbyList: List<HighScore>? = null
  init {
    camera.setToOrtho(false, Game.WIDTH, Game.HEIGHT)
    GlobalScope.launch {
      lobbyList = ApiClient.highScoreListRequest()
    }
  }
  private val viewport: Viewport = StretchViewport(Game.WIDTH, Game.HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.batch)
  private val background = Texture("initiate_game_state.jpg")
  val font: BitmapFont = Font.generateFont(20)
  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
  }
  val backButton = BackButton(stateManager, MenuState(stateManager), stage)
  override fun handleInput() {
  }
  override fun update(deltaTime: Float) {
  }
  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Game.WIDTH, Game.HEIGHT)
    font.draw(sprites, "HIGHSCORES", Game.WIDTH/2-70, Game.HEIGHT/2+70)
    lobbyList?.let {
      val xPosition: Float = Game.WIDTH / 2 - 150f
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Game.HEIGHT / 2) + 40f - (30f * lobbyIndex)
        font.draw(sprites, lobbyList!![lobbyIndex].name, xPosition, yPosition)
        font.draw(sprites, lobbyList!![lobbyIndex].value.toString(), xPosition + 230f, yPosition)
      }
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

    backButton.dispose()

    println("HighscoreListState disposed")
  }

}