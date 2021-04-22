package com.coronadefense.views

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.coronadefense.Game
import com.coronadefense.GameStage
import com.coronadefense.utils.Constants


class Intruder(
        texturePath: String,
        startPosition: Float,
        private val endPosition: Float,
        time: Float,
        val gameStage: GameStage
): MovingGameObject{
    private val shopWidth: Float = Constants.GAME_WIDTH/ 4
    private val cellWidth = (Constants.GAME_WIDTH - shopWidth) / gameStage!!.XSize
    private val cellHeight = Constants.GAME_HEIGHT / gameStage!!.YSize

    override val texture = Texture(texturePath)
    var currentPathPosition = startPosition
    val speed = (endPosition-startPosition)/time

    override fun draw(sprites: SpriteBatch){
        if(currentPathPosition <= endPosition){
            val position = gameStage.getPointAlongPath(currentPathPosition.toDouble())
            sprites.draw(texture, position.X.toFloat()*cellWidth-(cellWidth/2), position.Y.toFloat()*cellHeight-(cellWidth/2), cellWidth, cellHeight)
        }
    }
    override fun update(deltaTime: Float) {
        if(currentPathPosition<=endPosition){
            currentPathPosition += speed*deltaTime
        }
    }
}