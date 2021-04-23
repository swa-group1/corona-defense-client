package com.coronadefense.states.gameEndStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.receiver.messages.EndGameMessage
import com.coronadefense.states.InputState
import com.coronadefense.states.State
import com.coronadefense.states.StateManager
import com.coronadefense.utils.BackButton
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Font
import com.coronadefense.utils.Textures

class EndGameState (
        stateManager: StateManager,
        private val endGameMessage: EndGameMessage
): InputState(stateManager) {

    private val background: Texture  = if (endGameMessage.victory) Texture(Textures.background("win"))else Texture(Textures.background("loss"))

    init{
        textures += background
    }

    private val backButton = BackButton("MainMenu", stateManager, stage)

    private val font = Font(20)
    private val bigFont = Font(30)

    private val score: String = "Score: ${endGameMessage.score}"
    private var highScore: String = ""

    override fun update(deltaTime: Float) {
        backButton.update()
    }

    override fun render(sprites: SpriteBatch) {
        sprites.projectionMatrix = camera.combined
        sprites.begin()
        sprites.draw(background, 0f, 0f, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
        when(endGameMessage.onHighScoreList){
            in 1..10 -> highScore = "You made the highscore list, your current position is ${endGameMessage.onHighScoreList}"
            0 -> "You did not make it onto the highscore list."
            -1 -> "Try HARD mode to get on the highscore list."
        }
        font.draw(
                sprites,
                highScore,
                (Constants.GAME_WIDTH - font.width(highScore))/2,
                Constants.GAME_HEIGHT /3
        )

        bigFont.draw(
                sprites,
                score,
                (Constants.GAME_WIDTH - bigFont.width(score))/2 ,
                Constants.GAME_HEIGHT /5
        )
        sprites.end()
        super.draw()
    }

    override fun dispose(){
        super.dispose()
        font.dispose()
        bigFont.dispose()
    }
}