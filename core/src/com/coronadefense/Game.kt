package com.coronadefense

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
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
    }
    override fun render() {
        Gdx.gl.glClearColor(1f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        batch?.begin()
        if (highScores != null) {
            for (i in highScores!!.indices) {
                font!!.draw(
                  batch, "${highScores!![i].name}: ${highScores!![i].value}", 10f, 30f * (i + 1)
                )
            }
        }
        batch?.end()
    }

    override fun dispose() {
        batch?.dispose()
        img?.dispose()
    }
}