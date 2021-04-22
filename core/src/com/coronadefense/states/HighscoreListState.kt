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
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HighscoreListState (stateManager: GameStateManager): State(stateManager){
  init {
    camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)

  }
  private val viewport: Viewport = StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera)
  private val stage: Stage = Stage(viewport, Game.sprites)

  private val background = Texture("initiate_game_state.jpg")
  private val font: BitmapFont = Font.generateFont(20)

  var lobbyList: List<HighScore>? = null
  init {
    GlobalScope.launch {
      lobbyList = ApiClient.highScoreListRequest()
    }
  }

  init {
    val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer;
    if (!inputMultiplexer.processors.contains(stage)) {
      inputMultiplexer.addProcessor(stage)
    }
  }
  val backButton = BackButton("MainMenu", stateManager, stage)

  override fun update(deltaTime: Float) {
    backButton.update()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()
    sprites.draw(background, 0F, 0F, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    font.draw(sprites, "HIGHSCORES", Constants.GAME_WIDTH / 2 - 70, Constants.GAME_HEIGHT / 2 + 70)
    lobbyList?.let {
      val xPosition: Float = Constants.GAME_WIDTH / 2 - 150f
      for (lobbyIndex in lobbyList!!.indices) {
        val yPosition: Float = (Constants.GAME_HEIGHT / 2) + 40f - (30f * lobbyIndex)
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