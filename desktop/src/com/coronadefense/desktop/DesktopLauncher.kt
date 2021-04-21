package com.coronadefense.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.coronadefense.Game

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.width = Game.WIDTH.toInt()
        config.height = Game.HEIGHT.toInt()
        config.title = Game.TITLE
        LwjglApplication(Game(), config)
    }
}