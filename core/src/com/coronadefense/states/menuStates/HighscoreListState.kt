package com.coronadefense.states.menuStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.states.GameStateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.HighScore
import com.coronadefense.states.StageState
import com.coronadefense.states.State
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures
import kotlinx.coroutines.runBlocking

/**
 * State to show the list of highscores between players in Corona Defense.
 * Extends BackgroundState to show the menu background.
 * @param stateManager Manager of all game states.
 */
class HighscoreListState(
  stateManager: GameStateManager
): StageState(stateManager){
  private val background: Texture = Texture(Textures.background("menu"))

  // creates a font of size 20 for displaying text and title
  private val font: BitmapFont = Font.generateFont(20)
  val title = "HIGHSCORES"

  //declares highscores, then fetches them from the API
  var highscoreList: List<HighScore>? = null
  init {
    runBlocking {
      highscoreList = ApiClient.highScoreListRequest()
    }
  }
  // adds a back button to the stage which directs back to the main menu
  val backButton = BackButton("MainMenu", stateManager, stage)

  // updates the back button
  override fun update(deltaTime: Float) {
    backButton.update()
  }

  // renders the background, a title, and name + score for all highscores
  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0f, 0f, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)

    font.draw(
      sprites,
      title,
      (Constants.GAME_WIDTH - Font.textWidth(font, title)) / 2,
      Constants.GAME_HEIGHT / 2 + 70
    )

    highscoreList?.let {
      val xPosition: Float = (Constants.GAME_WIDTH - Constants.LIST_BUTTON_WIDTH) / 2
      for (highscoreIndex in highscoreList!!.indices) {
        val yPosition: Float = (Constants.GAME_HEIGHT / 2) + 40f - (Constants.LIST_BUTTON_HEIGHT * highscoreIndex)
        font.draw(sprites, highscoreList!![highscoreIndex].name, xPosition, yPosition)
        font.draw(sprites, highscoreList!![highscoreIndex].value.toString(), xPosition + Constants.LIST_BUTTON_WIDTH, yPosition)
      }
    }

    sprites.end()
    super.render(sprites)
  }

  override fun dispose() {
    super.dispose()

    background.dispose()
    font.dispose()
    backButton.dispose()

    println("HighscoreListState disposed")
  }
}