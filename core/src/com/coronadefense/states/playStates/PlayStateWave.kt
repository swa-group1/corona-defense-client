package com.coronadefense.states.playStates

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.coronadefense.states.StateManager
import com.coronadefense.receiver.messages.*
import com.coronadefense.states.GameObserver
import com.coronadefense.states.InputState
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
) : InputState(stateManager) {
  private val sidebarTexture: Texture = Texture(Textures.background("sidebar"))

  private val stageMapTexture: Texture = Texture(Textures.stage(gameObserver.gameStage!!.Number))
  private val stageMap = Image(stageMapTexture)

  init {
    stageMap.setSize(GAME_WIDTH - SIDEBAR_WIDTH, GAME_HEIGHT)
    stageMap.setPosition(0f, 0f)
    stage.addActor(stageMap)
  }

  private var time: Float = 0f //time passed
  private var nextRound: Int = 0 // number of next round, set when all animations are received

  private var movingGameObjects = mutableListOf<MovingGameObject>()

  override fun update(deltaTime: Float) {
    val removePathAnimations = mutableListOf<PathToPathAnimationMessage>()
    val removeBoardAnimations = mutableListOf<BoardToPathAnimationMessage>()
    if (time + deltaTime <= gameObserver.timeConfirmed) {
      val newTime = time + deltaTime
      time += deltaTime
      for (message in gameObserver.pathToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Intruder(
            message.spriteNumber,
            message.startPosition,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          removePathAnimations += message
        }
      }
      for (message in gameObserver.boardToPathAnimations) {
        if (message.startTime < newTime) {
          movingGameObjects.add(Projectile(
            message.spriteNumber,
            message.startX,
            message.startY,
            message.endPosition,
            message.endTime - message.startTime,
            gameObserver.gameStage!!
          ))
          removeBoardAnimations += message
        }
      }
      for (movingGameObject in movingGameObjects) {
        movingGameObject.update(deltaTime)
      }
    } else if (gameObserver.timeConfirmed > 0) {
      stateManager.set(PlayStatePlacement(stateManager, gameObserver))
    }
    for (message in removePathAnimations) {
      gameObserver.pathToPathAnimations -= message
    }
    for (message in removeBoardAnimations) {
      gameObserver.boardToPathAnimations -= message
    }
  }

  override fun render(sprites: SpriteBatch) {
    sprites.projectionMatrix = camera.combined

    sprites.begin()
    sprites.draw(sidebarTexture, GAME_WIDTH - SIDEBAR_WIDTH, 0f, SIDEBAR_WIDTH, GAME_HEIGHT)
    sprites.end()

    super.draw()

    sprites.begin()
    for (tower in gameObserver.placedTowers) {
      sprites.draw(
              Texture(Textures.tower(tower.type)),
              tower.position.x * gameObserver.gameStage!!.tileWidth,
              tower.position.y * gameObserver.gameStage!!.tileHeight,
              gameObserver.gameStage!!.tileWidth,
              gameObserver.gameStage!!.tileHeight
      )
    }
    for (movingGameObject in movingGameObjects) {
      movingGameObject.draw(sprites)
    }
    sprites.end()
  }

  override fun dispose() {
    super.dispose()

    for (movingGameObject in movingGameObjects) {
      movingGameObject.dispose()
    }
  }
}