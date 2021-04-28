package com.coronadefense.states.menuStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.HighScore
import com.coronadefense.states.InputState
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


/**
 * State to show the list of highscores between players in Corona Defense.
 * Extends InputState to allow user input on the back button.
 * @param stateManager Manager of all game states.
 */
class HighscoreListState(
  stateManager: StateManager
): InputState(stateManager){
  private val backButton = BackButton("MainMenu", stateManager, stage)

  private val font = Font(20)
  private val title = "HIGHSCORES"

  private var highscoreList: List<HighScore>? = null

  // Calculates positions here to spare calculations in the render method.
  private val listPositionX: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) * 0.5f
  private val listPositionsY: MutableList<Float> = mutableListOf()

  init {
    // Launches a coroutine to fetch the high score list, and calculates the list items' Y positions based on its length.
    GlobalScope.launch {
      highscoreList = ApiClient.highScoreListRequest()
      for (index in highscoreList!!.indices) {
        listPositionsY += GAME_HEIGHT * 0.5f + MENU_TITLE_OFFSET - LIST_ITEM_HEIGHT * (index + 1)
      }
    }
  }

  override fun update(deltaTime: Float) {
    backButton.update()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(Textures.background("menu"), 0f, 0f, GAME_WIDTH, GAME_HEIGHT)
    backButton.render(sprites)

    font.draw(
      sprites,
      title,
      (GAME_WIDTH - font.width(title)) * 0.5f,
      (GAME_HEIGHT - font.height(title)) * 0.5f + MENU_TITLE_OFFSET
    )

    // Checks that the highscoreList is fetched, then loops through it to display each highscore.
    highscoreList?.let {
      for ((index, highscore) in highscoreList!!.withIndex()) {
        font.draw(
          sprites,
          highscore.name,
          listPositionX,
          listPositionsY[index] - font.height(highscore.name) * 0.5f
        )
        val scoreText = highscore.value.toString()
        font.draw(
          sprites,
          scoreText,
          listPositionX + LIST_ITEM_WIDTH - font.width(scoreText),
          listPositionsY[index] - font.height(scoreText) * 0.5f
        )
      }
    }

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    font.dispose()
    backButton.dispose()
  }
}