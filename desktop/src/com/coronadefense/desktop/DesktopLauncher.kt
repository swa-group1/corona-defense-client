package com.coronadefense.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.coronadefense.Game
import com.coronadefense.utils.Constants

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = Constants.GAME_TITLE
        config.width = Constants.GAME_WIDTH.toInt()
        config.height = Constants.GAME_HEIGHT.toInt()
        LwjglApplication(Game(), config)
    }
}