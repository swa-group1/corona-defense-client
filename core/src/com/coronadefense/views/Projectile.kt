package com.coronadefense.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.coronadefense.Game
import com.coronadefense.GameStage
import kotlin.math.*
import com.coronadefense.utils.Constants


class Projectile(
        texturePath: String,
        val startX: Int,
        val startY: Int,
        private val endPosition: Float,
        val time: Float,
        val gameStage: GameStage
): MovingGameObject{
    private val currentPosition = Vector2(startX.toFloat(), startY.toFloat())
    private val targetPosition = gameStage.getPointAlongPath(endPosition.toDouble())

    private var currentTime = 0f

    private val shopWidth: Float = Constants.GAME_WIDTH / 4
    private val cellWidth = (Constants.GAME_WIDTH - shopWidth) / gameStage.XSize
    private val cellHeight = Constants.GAME_HEIGHT / gameStage.YSize

    override val texture = Texture(texturePath)


    override fun draw(sprites: SpriteBatch){
        if(currentTime<= time){
            sprites.draw(texture, currentPosition.x*cellWidth-(cellWidth/2), currentPosition.y*cellHeight-(cellHeight/2), cellWidth, cellHeight)
        }

    }
    override fun update(deltaTime: Float) {
        if (currentTime<= time){
            currentTime+=deltaTime
            val s= currentTime/time
            currentPosition.x = startX+(targetPosition.X.toFloat()-startX)*s
            currentPosition.y = startY+(targetPosition.Y.toFloat()-startY)*s
        }
    }
}