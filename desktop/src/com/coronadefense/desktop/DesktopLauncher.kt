package com.coronadefense.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.coronadefense.Game
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_TITLE
import com.coronadefense.utils.Constants.GAME_WIDTH

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = GAME_TITLE
        config.width = GAME_WIDTH.toInt()
        config.height = GAME_HEIGHT.toInt()
        LwjglApplication(Game(), config)
    }
}