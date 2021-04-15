package com.coronadefense

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.GL20
<<<<<<< HEAD
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.api.ApiClient
import com.coronadefense.api.HighScore
import kotlinx.coroutines.*

class Game : ApplicationAdapter() {
    var batch: SpriteBatch? = null
    var img: Texture? = null
    val apiClient: ApiClient = ApiClient()
    var highScores: List<HighScore>? = null
    var font: BitmapFont? = null
    override fun create() {
        batch = SpriteBatch()
        font = BitmapFont()
        GlobalScope.launch {
            highScores = apiClient.getHighScoreList()
        }
=======
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.states.MenuState

class Game : ApplicationAdapter() {
    companion object {
        const val WIDTH = 800F
        const val HEIGHT = 480F
        const val TITLE = "Game"
        var batch: SpriteBatch? = null
    }
    private var stateManager: GameStateManager? = null
    override fun create() {
        batch = SpriteBatch()
        stateManager = GameStateManager()
        Gdx.input.inputProcessor = InputMultiplexer()
        Gdx.gl.glClearColor(0F, 0F, 0F, 1F)
        stateManager?.push(MenuState(stateManager!!))
>>>>>>> main
    }
    override fun render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
<<<<<<< HEAD
        batch?.begin()
        if (highScores != null) {
            for (i in highScores!!.indices) {
                font!!.draw(
                  batch, "${highScores!![i].name}: ${highScores!![i].value}", 10f, 30f * (i + 1)
                )
            }
        }
        batch?.end()
=======
        stateManager?.update(Gdx.graphics.deltaTime)
        stateManager?.render(batch!!)
>>>>>>> main
    }

    override fun dispose() {
        batch?.dispose()
    }
}