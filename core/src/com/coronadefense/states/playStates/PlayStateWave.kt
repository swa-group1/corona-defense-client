package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coronadefense.Game
import com.coronadefense.types.GameStage
import com.coronadefense.states.StateManager
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.GameObserver
import com.coronadefense.states.ObserverState
import com.coronadefense.types.gameObjects.Intruder
import com.coronadefense.types.gameObjects.MovingGameObject
import com.coronadefense.types.gameObjects.Projectile
import com.coronadefense.utils.Constants.GAME_HEIGHT
import com.coronadefense.utils.Constants.GAME_WIDTH
import com.coronadefense.utils.Constants.SIDEBAR_WIDTH
import com.coronadefense.utils.Textures

class PlayStateWave(
  stateManager: StateManager,
  private val gameObserver: GameObserver
) : ObserverState(stateManager) {
  private val stageMapTexture: Texture = Texture(Textures.stage(gameObserver.gameStage.Number))
  private val stageMap = Image(stageMapTexture)

  init {
    println("PLAYSTATEWAVE")
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)
  }

  private var time: Float = 0f //time passed
  private var timeConfirmed: Float = 0f // time confirmed animations
  private var nextRound: Int = 0 // number of next round, set when all animations are received

  private var pathToPathAnimations = mutableListOf<PathToPathAnimationMessage>() //Intruders
  private var healthAnimations = mutableListOf<HealthAnimationMessage>()
  private var moneyAnimations = mutableListOf<MoneyAnimationMessage>()
  private var boardToPathAnimations = mutableListOf<BoardToPathAnimationMessage>()

  private var movingGameObjects = mutableListOf<MovingGameObject>()

  override fun handleHealthAnimationMessage(message: HealthAnimationMessage) {
    super.handleHealthAnimationMessage(message)
    healthAnimations.add(message)
  }

  override fun handleMoneyAnimationMessage(message: MoneyAnimationMessage) {
    super.handleMoneyAnimationMessage(message)
    moneyAnimations.add(message)
  }

  override fun handlePathToPathAnimationMessage(message: PathToPathAnimationMessage) {
    super.handlePathToPathAnimationMessage(message)
    pathToPathAnimations.add(message)
  }

  override fun handleAnimationConfirmationMessage(message: AnimationConfirmationMessage) {
    super.handleAnimationConfirmationMessage(message)
    timeConfirmed = message.time
  }

  override fun handleBoardToPathAnimationMessage(message: BoardToPathAnimationMessage) {
    super.handleBoardToPathAnimationMessage(message)
    boardToPathAnimations.add(message)
  }

  override fun update(deltaTime: Float) {
    var removePathAnimations = mutableListOf<PathToPathAnimationMessage>()
    var removeBoardAnimations = mutableListOf<BoardToPathAnimationMessage>()
    if (time + deltaTime <= timeConfirmed) {
      var newTime = time + deltaTime
      time += deltaTime
      for (message in pathToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Intruder(Textures.intruder(message.spriteNumber), message.startPosition, message.endPosition, message.endTime - message.startTime, gameStage))
          removePathAnimations.add(message)
        }
      }
      for (message in boardToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Projectile(Textures.projectile(message.spriteNumber), message.startX, message.startY, message.endPosition, message.endTime - message.startTime, gameStage))
          removeBoardAnimations.add(message)
        }
      }

      for (movingGameObject in movingGameObjects) {
        movingGameObject.update(deltaTime)
      }
    } else if (timeConfirmed > 0) {
      val playState = PlayStatePlacement(stateManager, lobby, gameStage.Number)
      Game.receiver.addObserver(playState)
      Game.receiver.removeObserver(this)
      stateManager.set(playState)
    }
    for (message in removePathAnimations) {
      pathToPathAnimations.remove(message)
    }
    for (message in removeBoardAnimations) {
      boardToPathAnimations.remove(message)
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined
    super.draw()
    sprites.begin()

    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }

    sprites.end()
  }

  override fun dispose() {
    super.dispose()
  }
}