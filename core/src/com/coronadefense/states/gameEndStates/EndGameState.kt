package com.coronadefense.states.gameEndStates

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.receiver.messages.EndGameMessage
import com.coronadefense.states.InputState
import com.coronadefense.states.StateManager
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures

/**
 * State for when the game ends, showing either a victory or a defeat screen.
 * Extends InputState to enable user input on the back button.
 * @param stateManager Manager of all game states.
 * @param endGameMessage Message that triggered this state, to determine win/loss state.
 */
class EndGameState(
  stateManager: StateManager,
  endGameMessage: EndGameMessage
) : InputState(stateManager) {
  private val backgroundType = if (endGameMessage.victory) "win" else "loss"
  private val backButton = BackButton("MainMenu", stateManager, stage)

  private val font = Font(20)
  private val bigFont = Font(30)

  // Determines the message to display to the user based on their placing.
  private val highscoreMessage: String = when (endGameMessage.onHighScoreList) {
    in 1..10 -> "You made the highscore list, your current position is ${endGameMessage.onHighScoreList}!"
    0 -> "You did not make it onto the highscore list."
    -1 -> "Try HARD mode to get on the highscore list!"
    else -> ""
  }
  private val score: String = "Score: ${endGameMessage.score}"

  override fun update(deltaTime: Float) {
    backButton.update()
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    sprites.begin()

    sprites.draw(Textures.background(backgroundType), 0f, 0f, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    backButton.render(sprites)

    font.draw(
      sprites,
      highscoreMessage,
      (Constants.GAME_WIDTH - font.width(highscoreMessage)) * 0.5f,
      Constants.GAME_HEIGHT * 0.33f
    )

    bigFont.draw(
      sprites,
      score,
      (Constants.GAME_WIDTH - bigFont.width(score)) * 0.5f,
      Constants.GAME_HEIGHT * 0.2f
    )

    sprites.end()
    super.draw()
  }

  override fun dispose() {
    super.dispose()
    Textures.disposeAll()
    font.dispose()
    bigFont.dispose()
    backButton.dispose()
  }
}