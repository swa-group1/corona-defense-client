package com.coronadefense.states.menuStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.StateManager
import com.coronadefense.api.ApiClient
import com.coronadefense.api.HighScore
import com.coronadefense.states.InputState
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.LIST_ITEM_HEIGHT
import com.coronadefense.utils.Constants.LIST_ITEM_SPACING
import com.coronadefense.utils.Constants.LIST_ITEM_WIDTH
import com.coronadefense.utils.Constants.MENU_TITLE_OFFSET
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures
import kotlinx.coroutines.runBlocking

/**
 * State to show the list of highscores between players in Corona Defense.
 * @param stateManager Manager of all game states.
 */
class HighscoreListState(
  stateManager: StateManager
): InputState(stateManager){
  private val background: Texture = Texture(Textures.background("menu"))

  private val font = Font(20)
  private val title = "HIGHSCORES"

  private var highscoreList: List<HighScore>? = null
  init {
    runBlocking {
      highscoreList = ApiClient.highScoreListRequest()
    }
  }

  private val backButton = BackButton("MainMenu", stateManager, stage)

  override fun update(deltaTime: Float) {
    backButton.update()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(background, 0f, 0f, GAME_WIDTH, GAME_HEIGHT)

    font.draw(
      sprites,
      title,
      (GAME_WIDTH - font.width(title)) / 2,
      (GAME_HEIGHT - font.height(title)) / 2 + MENU_TITLE_OFFSET
    )

    highscoreList?.let {
      val xPosition: Float = (GAME_WIDTH - LIST_ITEM_WIDTH) / 2
      for (highscoreIndex in highscoreList!!.indices) {
        val yPosition: Float =
          (GAME_HEIGHT / 2) + MENU_TITLE_OFFSET - (LIST_ITEM_HEIGHT + LIST_ITEM_SPACING) * (highscoreIndex + 1)
        val nameText = highscoreList!![highscoreIndex].name
        font.draw(
          sprites,
          highscoreList!![highscoreIndex].name,
          xPosition,
          yPosition - font.height(nameText) / 2
        )
        val scoreText = highscoreList!![highscoreIndex].value.toString()
        font.draw(
          sprites,
          scoreText,
          xPosition + LIST_ITEM_WIDTH - font.width(scoreText),
          yPosition - font.height(scoreText) / 2
        )
      }
    }

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()

    background.dispose()
    font.dispose()
    backButton.dispose()

    println("HighscoreListState disposed")
  }
}