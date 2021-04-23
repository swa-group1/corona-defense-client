package com.coronadefense.states.playStates

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.badlogic.gdx.utils.viewport.Viewport
import com.coronadefense.Game
import com.coronadefense.GameStage
import com.coronadefense.states.GameStateManager
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.ObserverState
import com.coronadefense.types.Lobby
import com.coronadefense.types.gameObjects.Intruder
import com.coronadefense.types.gameObjects.MovingGameObject
import com.coronadefense.types.gameObjects.Projectile
import com.coronadefense.utils.Constants
import com.coronadefense.utils.Textures

class PlayStateWave (stateManager: GameStateManager, val lobby: Lobby, val round:Int, private val gameStage: GameStage) : ObserverState(stateManager) {
    init {
        camera.setToOrtho(false, Constants.GAME_WIDTH, Constants.GAME_HEIGHT)
    }
    private val viewport: Viewport = StretchViewport(Constants.GAME_WIDTH, Constants.GAME_HEIGHT, camera)
    private val stage: Stage = Stage(viewport, Game.sprites)

    private val shopWidth: Float = Constants.GAME_WIDTH / 4

    private val stageMapTexture: Texture = Texture(Textures.stage(gameStage.Number))
    private val stageMap = Image(stageMapTexture)

    private var time:Float = 0F //time passed
    private var timeConfirmed:Float = 0F //time confirmed animations
    private var nextRound: Int = 0 //number of next round, set when all animations are received

    private var pathToPathAnimations = mutableListOf<PathToPathAnimationMessage>() //Intruders
    private var healthAnimations = mutableListOf<HealthAnimationMessage>()
    private var moneyAnimations = mutableListOf<MoneyAnimationMessage>()
    private var boardToPathAnimations = mutableListOf<BoardToPathAnimationMessage>()

    private var movingGameObjects = mutableListOf<MovingGameObject>()

    init {
        val inputMultiplexer: InputMultiplexer = Gdx.input.inputProcessor as InputMultiplexer
        if (!inputMultiplexer.processors.contains(stage)) {
            inputMultiplexer.addProcessor(stage)
        }

        stageMap.setSize(Constants.GAME_WIDTH - shopWidth, Constants.GAME_HEIGHT)
        stageMap.setPosition(0f, 0f)
        stage.addActor(stageMap)
    }

    override fun handleHealthAnimationMessage(message: HealthAnimationMessage) {
        super.handleHealthAnimationMessage(message)
        healthAnimations.add(message)
    }

    override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage) {
        super.handleMoneyAnimationMessage(message)
        moneyAnimations.add(message)
    }

    override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage) {

        pathToPathAnimations.add(message)
    }

    override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage) {
        super.handleAnimationConfirmationMessage(message)
        timeConfirmed=message.time
    }

    override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage) {
        super.handleBoardToPathAnimationMessage(message)
        boardToPathAnimations.add(message)
    }

    override fun update(deltaTime: Float) {
        var removePathAnimations = mutableListOf<PathToPathAnimationMessage>()
        var removeBoardAnimations = mutableListOf<BoardToPathAnimationMessage>()
        if(time +deltaTime<=timeConfirmed){
            var newTime = time+deltaTime
            time +=deltaTime
            for (message in pathToPathAnimations){
                if(message.startTime<newTime){
                    movingGameObjects.add(Intruder(Textures.intruder(message.spriteNumber), message.startPosition, message.endPosition, message.endTime-message.startTime, gameStage))
                    removePathAnimations.add(message)
                }
            }
            for (message in boardToPathAnimations){
                if(message.startTime<newTime){
                    movingGameObjects.add(Projectile(Textures.projectile(message.spriteNumber), message.startX, message.startY, message.endPosition, message.endTime-message.startTime, gameStage))
                    removeBoardAnimations.add(message)
                }
            }

            for (movingGameObject in movingGameObjects){
                movingGameObject.update(deltaTime)
            }
        }
        else if(timeConfirmed>0){
            val playState = PlayStatePlacement(stateManager, lobby,gameStage.Number)
            Game.receiver.addObserver(playState)
            Game.receiver.removeObserver(this)
            stateManager.set(playState)
        }
        for(message in removePathAnimations){
            pathToPathAnimations.remove(message)
        }
        for(message in removeBoardAnimations){
            boardToPathAnimations.remove(message)
        }
    }

    override fun render(sprites: SpriteBatch) {
        stage.draw()
        sprites.begin()
        for(movingGameObject in movingGameObjects){
            movingGameObject.draw(sprites)
        }
        sprites.end()
    }


    override fun dispose() {

    }
}